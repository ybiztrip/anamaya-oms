package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.dto.request.BookingFlightListFilter;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.entity.BookingPax;
import ai.anamaya.service.oms.core.helper.json.JsonHelper;
import ai.anamaya.service.oms.core.repository.BookingFlightRepository;
import ai.anamaya.service.oms.core.repository.BookingPaxRepository;
import ai.anamaya.service.oms.core.specification.BookingFlightSpecification;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingFlightExportService {

    private static final String[] HEADER = {
        "REF ID",
        "Dokumen Perjalanan",
        "Departure Date",
        "Airline Flight",
        "Code",
        "Flight Type",
        "Departure City",
        "Departure Airport",
        "Departure Terminal",
        "Arrival City",
        "Arrival Airport",
        "Arrival Terminal",
        "Passanger Name",
        "Base Price",
        "Admin Fee",
        "Total Price",
        "Status"
    };
    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final byte[] UTF8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private static final String DASH = "-";

    private final BookingFlightRepository bookingFlightRepository;
    private final BookingPaxRepository bookingPaxRepository;
    private final JsonHelper jsonHelper;

    @Transactional(readOnly = true)
    public byte[] exportToCsv(String sort, BookingFlightListFilter filter) {
        Specification<BookingFlight> spec = BookingFlightSpecification.filter(filter);
        List<BookingFlight> flights = bookingFlightRepository.findAll(spec, buildSort(sort));

        Set<String> bookingCodes = flights.stream()
            .map(BookingFlight::getBookingCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Map<String, List<BookingPax>> paxByCode = bookingCodes.isEmpty()
            ? Map.of()
            : bookingPaxRepository.findByBookingCodeIn(bookingCodes).stream()
                .collect(Collectors.groupingBy(BookingPax::getBookingCode));

        return buildCsv(flights, paxByCode);
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
        List<BookingFlight> flights,
        Map<String, List<BookingPax>> paxByCode
    ) {
        StringBuilder sb = new StringBuilder();
        appendRow(sb, HEADER);

        for (BookingFlight f : flights) {
            JsonNode metadata = f.getMetadata() != null ? jsonHelper.toJsonNode(f.getMetadata()) : null;

            String refId = nullToEmpty(f.getBookingCode());
            String departureDate = f.getDepartureDatetime() != null
                ? f.getDepartureDatetime().format(DATE_FMT)
                : "";
            String airlineName = metadataText(metadata, "airlineName");
            String flightNo = metadataText(metadata, "flightNo");
            String departureCity = metadataText(metadata, "departureCity");
            String departureAirport = nullToEmpty(f.getOrigin());
            String departureTerminal = metadataText(metadata, "departureTerminal");
            String arrivalCity = metadataText(metadata, "arrivalCity");
            String arrivalAirport = nullToEmpty(f.getDestination());
            String arrivalTerminal = metadataText(metadata, "arrivalTerminal");
            String passengerName = guestNames(paxByCode.get(f.getBookingCode()));

            BigDecimal basePrice = f.getTotalAmount() != null ? f.getTotalAmount() : BigDecimal.ZERO;
            BigDecimal adminFee = f.getManagementFeeAmount() != null
                ? f.getManagementFeeAmount()
                : BigDecimal.ZERO;
            BigDecimal totalPrice = basePrice.add(adminFee);

            String status = f.getStatus() != null ? f.getStatus().name() : "";

            appendRow(sb, new String[]{
                refId,
                DASH,
                departureDate,
                airlineName,
                flightNo,
                DASH,
                departureCity,
                departureAirport,
                departureTerminal,
                arrivalCity,
                arrivalAirport,
                arrivalTerminal,
                passengerName,
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
