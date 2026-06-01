package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.dto.request.BookingHotelListFilter;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.entity.BookingPax;
import ai.anamaya.service.oms.core.helper.json.JsonHelper;
import ai.anamaya.service.oms.core.repository.BookingHotelRepository;
import ai.anamaya.service.oms.core.repository.BookingPaxRepository;
import ai.anamaya.service.oms.core.repository.BookingRepository;
import ai.anamaya.service.oms.core.specification.BookingHotelSpecification;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingHotelExportService {

    private static final String[] HEADER = {
        "REF ID",
        "Dokumen Perjalanan",
        "Booker",
        "Hotel",
        "Room",
        "Location",
        "Check In",
        "Check Out",
        "Guest Name",
        "Base Price",
        "Admin Fee",
        "Total Price",
        "Status"
    };
    private static final byte[] UTF8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private static final String DASH = "-";

    private final BookingHotelRepository bookingHotelRepository;
    private final BookingRepository bookingRepository;
    private final BookingPaxRepository bookingPaxRepository;
    private final JsonHelper jsonHelper;

    @Transactional(readOnly = true)
    public byte[] exportToCsv(String sort, BookingHotelListFilter filter) {
        Specification<BookingHotel> spec = BookingHotelSpecification.filter(filter);
        List<BookingHotel> hotels = bookingHotelRepository.findAll(spec, buildSort(sort));

        Set<Long> bookingIds = hotels.stream()
            .map(BookingHotel::getBookingId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Set<String> bookingCodes = hotels.stream()
            .map(BookingHotel::getBookingCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Map<Long, Booking> bookingsById = bookingIds.isEmpty()
            ? Map.of()
            : bookingRepository.findAllById(bookingIds).stream()
                .collect(Collectors.toMap(Booking::getId, Function.identity(), (a, b) -> a));

        Map<String, List<BookingPax>> paxByCode = bookingCodes.isEmpty()
            ? Map.of()
            : bookingPaxRepository.findByBookingCodeIn(bookingCodes).stream()
                .collect(Collectors.groupingBy(BookingPax::getBookingCode));

        return buildCsv(hotels, bookingsById, paxByCode);
    }

    private Sort buildSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by("createdAt").descending();
        }
        String[] parts = sort.split(";");
        String field = parts[0];
        Sort.Direction direction = (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

    private byte[] buildCsv(
        List<BookingHotel> hotels,
        Map<Long, Booking> bookingsById,
        Map<String, List<BookingPax>> paxByCode
    ) {
        StringBuilder sb = new StringBuilder();
        appendRow(sb, HEADER);

        for (BookingHotel h : hotels) {
            Booking booking = h.getBookingId() != null ? bookingsById.get(h.getBookingId()) : null;
            JsonNode metadata = h.getMetadata() != null ? jsonHelper.toJsonNode(h.getMetadata()) : null;

            String refId = nullToEmpty(h.getBookingCode());
            String booker = booking != null ? nullToEmpty(booking.getContactEmail()) : "";
            String hotelName = metadataText(metadata, "name");
            String roomName = metadataText(metadata, "roomName");
            String checkIn = h.getCheckInDate() != null ? h.getCheckInDate().toString() : "";
            String checkOut = h.getCheckOutDate() != null ? h.getCheckOutDate().toString() : "";
            String guestName = guestNames(paxByCode.get(h.getBookingCode()));

            BigDecimal basePrice = h.getPartnerSellAmount() != null
                ? BigDecimal.valueOf(h.getPartnerSellAmount())
                : BigDecimal.ZERO;
            BigDecimal adminFee = h.getManagementFeeAmount() != null
                ? h.getManagementFeeAmount()
                : BigDecimal.ZERO;
            BigDecimal totalPrice = basePrice.add(adminFee);

            String status = h.getStatus() != null ? h.getStatus().name() : "";

            appendRow(sb, new String[]{
                refId,
                DASH,
                booker,
                hotelName,
                roomName,
                "",
                checkIn,
                checkOut,
                guestName,
                basePrice.toPlainString(),
                adminFee.toPlainString(),
                totalPrice.toPlainString(),
                status
            });
        }

        byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[UTF8_BOM.length + body.length];
        System.arraycopy(UTF8_BOM, 0, out, 0, UTF8_BOM.length);
        System.arraycopy(body, 0, out, UTF8_BOM.length, body.length);
        return out;
    }

    private static String guestNames(List<BookingPax> paxes) {
        if (paxes == null || paxes.isEmpty()) {
            return "";
        }
        return paxes.stream()
            .map(p -> (nullToEmpty(p.getFirstName()) + " " + nullToEmpty(p.getLastName())).trim())
            .filter(s -> !s.isEmpty())
            .collect(Collectors.joining("; "));
    }

    private String metadataText(JsonNode metadata, String field) {
        if (metadata == null || !metadata.hasNonNull(field)) {
            return "";
        }
        return metadata.get(field).asText();
    }

    private static void appendRow(StringBuilder sb, String[] cells) {
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(cells[i]));
        }
        sb.append('\n');
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String escape(String s) {
        if (s == null) return "";
        boolean needsQuotes = s.indexOf(',') >= 0
            || s.indexOf('"') >= 0
            || s.indexOf('\n') >= 0
            || s.indexOf('\r') >= 0;
        if (!needsQuotes) return s;
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}
