package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.dto.request.CreditMonitoringFilter;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.entity.CompanyCredit;
import ai.anamaya.service.oms.core.entity.CompanyCreditDetail;
import ai.anamaya.service.oms.core.enums.BookingType;
import ai.anamaya.service.oms.core.enums.CreditTransactionType;
import ai.anamaya.service.oms.core.repository.BookingFlightRepository;
import ai.anamaya.service.oms.core.repository.BookingHotelRepository;
import ai.anamaya.service.oms.core.repository.BookingRepository;
import ai.anamaya.service.oms.core.repository.CompanyCreditDetailRepository;
import ai.anamaya.service.oms.core.repository.CompanyCreditRepository;
import ai.anamaya.service.oms.core.specification.CompanyCreditDetailSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditMonitoringExportService {

    private static final String[] HEADER = {
        "ID LAPORAN",
        "Transaction Date",
        "PNR Code / Itinerary Code",
        "REFERENCE ID",
        "Booker",
        "Description",
        "Status",
        "Currency",
        "Debit",
        "Credit",
        "Ending Balance"
    };
    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final byte[] UTF8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private static final String ZERO = "0";

    private final CompanyCreditRepository creditRepository;
    private final CompanyCreditDetailRepository detailRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final BookingHotelRepository bookingHotelRepository;
    private final BookingRepository bookingRepository;

    @Transactional(readOnly = true)
    public byte[] exportToCsv(CreditMonitoringFilter filter) {
        Long balanceId = null;
        if (filter.getCreditCodeType() != null && filter.getCompanyId() != null) {
            Optional<CompanyCredit> credit = creditRepository.findByCompanyIdAndCode(
                filter.getCompanyId(), filter.getCreditCodeType());
            if (credit.isEmpty()) {
                return buildCsv(List.of(), Map.of(), Map.of(), Map.of());
            }
            balanceId = credit.get().getId();
        }

        List<CompanyCreditDetail> details = detailRepository.findAll(
            CompanyCreditDetailSpecification.filter(filter, balanceId),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Set<String> flightCodes = details.stream()
            .filter(d -> d.getBookingType() == BookingType.FLIGHT)
            .map(CompanyCreditDetail::getReferenceCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Set<String> hotelCodes = details.stream()
            .filter(d -> d.getBookingType() == BookingType.HOTEL)
            .map(CompanyCreditDetail::getReferenceCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Map<String, BookingFlight> flightsByCode = flightCodes.isEmpty()
            ? Map.of()
            : bookingFlightRepository.findByBookingCodeIn(flightCodes).stream()
                .collect(Collectors.toMap(
                    BookingFlight::getBookingCode,
                    Function.identity(),
                    (a, b) -> a
                ));

        Map<String, BookingHotel> hotelsByCode = hotelCodes.isEmpty()
            ? Map.of()
            : bookingHotelRepository.findByBookingCodeIn(hotelCodes).stream()
                .collect(Collectors.toMap(
                    BookingHotel::getBookingCode,
                    Function.identity(),
                    (a, b) -> a
                ));

        Set<Long> bookingIds = new HashSet<>();
        flightsByCode.values().forEach(f -> {
            if (f.getBookingId() != null) bookingIds.add(f.getBookingId());
        });
        hotelsByCode.values().forEach(h -> {
            if (h.getBookingId() != null) bookingIds.add(h.getBookingId());
        });

        Map<Long, Booking> bookingsById = Map.of();
        if (!bookingIds.isEmpty() && filter.getCompanyId() != null) {
            bookingsById = bookingRepository
                .findByIdInAndCompanyId(new ArrayList<>(bookingIds), filter.getCompanyId())
                .stream()
                .collect(Collectors.toMap(Booking::getId, Function.identity()));
        }

        return buildCsv(details, flightsByCode, hotelsByCode, bookingsById);
    }

    private byte[] buildCsv(
        List<CompanyCreditDetail> details,
        Map<String, BookingFlight> flightsByCode,
        Map<String, BookingHotel> hotelsByCode,
        Map<Long, Booking> bookingsById
    ) {
        StringBuilder sb = new StringBuilder();
        appendRow(sb, HEADER);

        for (CompanyCreditDetail d : details) {
            BookingFlight flight = d.getBookingType() == BookingType.FLIGHT && d.getReferenceCode() != null
                ? flightsByCode.get(d.getReferenceCode())
                : null;
            BookingHotel hotel = d.getBookingType() == BookingType.HOTEL && d.getReferenceCode() != null
                ? hotelsByCode.get(d.getReferenceCode())
                : null;

            Long bookingId = flight != null ? flight.getBookingId()
                : hotel != null ? hotel.getBookingId()
                : null;
            Booking booking = bookingId != null ? bookingsById.get(bookingId) : null;

            String idLaporan = d.getId() != null ? d.getId().toString() : "";
            String txnDate = d.getCreatedAt() != null ? d.getCreatedAt().format(DATE_FMT) : "";
            String pnrItinerary = flight != null ? nullToEmpty(flight.getPnrInfo())
                : hotel != null ? nullToEmpty(hotel.getItineraryId())
                : "";
            String referenceId = flight != null ? nullToEmpty(flight.getBookingCode())
                : hotel != null ? nullToEmpty(hotel.getBookingCode())
                : "";
            String booker = booking != null ? nullToEmpty(booking.getContactEmail()) : "";
            String description = nullToEmpty(d.getRemarks());
            String status = flight != null && flight.getStatus() != null ? flight.getStatus().name()
                : hotel != null && hotel.getStatus() != null ? hotel.getStatus().name()
                : "";
            String debit = d.getType() == CreditTransactionType.DEBIT && d.getAmount() != null
                ? d.getAmount().toPlainString() : ZERO;
            String credit = d.getType() == CreditTransactionType.CREDIT && d.getAmount() != null
                ? d.getAmount().toPlainString() : ZERO;
            String endingBalance = d.getEndBalance() != null ? d.getEndBalance().toPlainString() : "";

            appendRow(sb, new String[]{
                idLaporan,
                txnDate,
                pnrItinerary,
                referenceId,
                booker,
                description,
                status,
                "IDR",
                debit,
                credit,
                endingBalance
            });
        }

        byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[UTF8_BOM.length + body.length];
        System.arraycopy(UTF8_BOM, 0, out, 0, UTF8_BOM.length);
        System.arraycopy(body, 0, out, UTF8_BOM.length, body.length);
        return out;
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
