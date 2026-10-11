package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.biztrip.BiztripTrainBookingService;
import ai.anamaya.service.oms.core.client.biztrip.dto.train.BiztripTrainBookingData;
import ai.anamaya.service.oms.core.client.queue.BookingPubSubPublisher;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.pubsub.BookingStatusMessage;
import ai.anamaya.service.oms.core.dto.request.BookingTrainRequest;
import ai.anamaya.service.oms.core.dto.request.BookingTrainSeatMapRequest;
import ai.anamaya.service.oms.core.dto.request.BookingTrainSetSeatRequest;
import ai.anamaya.service.oms.core.dto.request.BookingTrainSubmitRequest;
import ai.anamaya.service.oms.core.dto.response.BookingResponse;
import ai.anamaya.service.oms.core.entity.*;
import ai.anamaya.service.oms.core.enums.*;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.helper.json.JsonHelper;
import ai.anamaya.service.oms.core.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingTrainService {

    // ponytail: Biztrip train times are WIB wall time; compare against WIB whatever the server zone is
    private static final ZoneId BIZTRIP_ZONE = ZoneId.of("Asia/Jakarta");
    private static final DateTimeFormatter BIZTRIP_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    // ponytail: give Biztrip this long after timeLimit to report Ticketed before refunding the wallet
    private static final long ISSUING_GRACE_MINUTES = 30;
    private static final Set<BookingTrainStatus> OPEN_STATUSES = EnumSet.of(BookingTrainStatus.RESERVED, BookingTrainStatus.BOOKED);

    private final BookingApprovalRepository bookingApprovalRepository;
    private final BookingCommonService bookingCommonService;
    private final BookingTrainRepository bookingTrainRepository;
    private final BookingTrainHistoryRepository bookingTrainHistoryRepository;
    private final BookingPaxRepository bookingPaxRepository;
    private final BookingService bookingService;
    private final BookingPaxService bookingPaxService;
    private final BookingPubSubPublisher bookingPubSubPublisher;
    private final CompanyConfigRepository companyConfigRepository;
    private final JsonHelper jsonHelper;
    private final BiztripTrainBookingService biztripTrainBookingService;

    @Transactional(rollbackFor = Exception.class)
    public BookingResponse submitBookingTrain(CallerContext callerContext, Long bookingId, BookingTrainSubmitRequest request) {
        Long userId = callerContext.userId();
        Booking booking = bookingService.getValidatedBooking(callerContext, bookingId);

        if (!booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AccessDeniedException("This booking journey is not approved.");
        }

        BookingTrainRequest req = request.getTrain();
        if (req.getPaymentMethod() == BookingPaymentMethod.CUST_CREDIT_CARD
            || !bookingCommonService.validateBookingPaymentMethod(callerContext, req.getPaymentMethod(), BookingType.TRAIN)) {
            throw new IllegalArgumentException("Invalid payment method");
        }

        LocalDate departureDate = req.getDepartureDatetime().toLocalDate();
        if (departureDate.isBefore(booking.getStartDate()) || departureDate.isAfter(booking.getEndDate())) {
            throw new IllegalArgumentException("Booking date train is outside journey date");
        }

        String bookingCode = "ANMT:" + Instant.now().toEpochMilli();
        BookingTrain train = bookingTrainRepository.save(BookingTrain.builder()
            .companyId(booking.getCompanyId())
            .bookingId(bookingId)
            .bookingCode(bookingCode)
            .classKey(req.getClassKey())
            .journeyCode(req.getJourneyCode())
            .carrierNumber(req.getCarrierNumber())
            .trainName(req.getTrainName())
            .subClass(req.getSubClass())
            .seatClass(req.getSeatClass())
            .provider(req.getProvider())
            .origin(req.getOrigin())
            .destination(req.getDestination())
            .departureDatetime(req.getDepartureDatetime())
            .arrivalDatetime(req.getArrivalDatetime())
            .paymentMethod(req.getPaymentMethod())
            .status(BookingTrainStatus.CREATED)
            .metadata(jsonHelper.toJson(req.getMetadata()))
            .createdBy(userId)
            .updatedBy(userId)
            .build());

        bookingPaxService.submitBookingPax(callerContext, bookingId, bookingCode, request.getPaxs());
        List<BookingPax> paxs = bookingPaxRepository.findByBookingIdAndBookingCode(bookingId, bookingCode);

        // Throws on failure, rolling back the train and pax rows
        BiztripTrainBookingData data = biztripTrainBookingService.book(callerContext, buildBookRequest(booking, train, paxs));

        train.setBookingReference(data.getId());
        train.setReferenceCode(data.getReferenceCode());
        train.setOtaReference(data.getBookingCode());
        train.setTimeLimit(data.getTimeLimit() != null ? LocalDateTime.parse(data.getTimeLimit(), BIZTRIP_DATETIME) : null);
        train.setTotalAmount(data.totalPaid());
        train.setSeats(toSeats(data, paxs));
        train.setStatus(BookingTrainStatus.RESERVED);
        bookingTrainRepository.save(train);
        saveHistory(train, BookingTrainStatus.RESERVED, data);

        bookingCommonService.bookingTravelPolicy(callerContext, bookingCode, BookingType.TRAIN);

        return bookingService.toResponse(booking, true, false);
    }

    public List<Map<String, Object>> getSeatMap(CallerContext callerContext, Long bookingId, BookingTrainSeatMapRequest request) {
        bookingService.getValidatedBooking(callerContext, bookingId);
        BookingTrain train = getOpenTrain(bookingId, request.getTrainId());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", train.getBookingReference());
        body.put("origin", train.getOrigin());
        body.put("destination", train.getDestination());
        body.put("departureDate", train.getDepartureDatetime().format(DATE));
        body.put("carrierNumber", train.getCarrierNumber());
        body.put("subClass", train.getSubClass());
        body.put("provider", train.getProvider());

        return biztripTrainBookingService.seatMap(callerContext, body);
    }

    @Transactional(rollbackFor = Exception.class)
    public BookingResponse setSeat(CallerContext callerContext, Long bookingId, BookingTrainSetSeatRequest request) {
        Booking booking = bookingService.getValidatedBooking(callerContext, bookingId);
        BookingTrain train = getOpenTrain(bookingId, request.getTrainId());
        if (train.getStatus() != BookingTrainStatus.RESERVED) {
            throw new IllegalStateException("Train seat is already confirmed");
        }

        if (request.getSeats() != null && !request.getSeats().isEmpty()) {
            Map<Long, BookingPax> paxById = bookingPaxRepository.findByBookingIdAndBookingCode(bookingId, train.getBookingCode())
                .stream()
                .collect(Collectors.toMap(BookingPax::getId, Function.identity()));

            List<Map<String, Object>> wagonSeatNumbers = request.getSeats().stream()
                .map(seat -> {
                    BookingPax pax = paxById.get(seat.getPaxId());
                    if (pax == null) {
                        throw new IllegalArgumentException("Pax " + seat.getPaxId() + " is not in this train booking");
                    }
                    return Map.<String, Object>of(
                        "firstName", pax.getFirstName(),
                        "lastName", pax.getLastName(),
                        "coachName", seat.getCoachName(),
                        "seatNumber", seat.getSeatNumber()
                    );
                })
                .toList();

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("id", train.getBookingReference());
            body.put("referenceCode", train.getReferenceCode());
            body.put("provider", train.getProvider());
            body.put("trainSeatRequest", Map.of(
                "bookingCode", train.getOtaReference(),
                "wagonSeatNumbers", wagonSeatNumbers
            ));
            body.put("callbackUri", "");

            biztripTrainBookingService.setSeat(callerContext, body);

            Map<Long, Map<String, Object>> seatsByPax = new LinkedHashMap<>();
            Optional.ofNullable(train.getSeats()).orElse(List.of())
                .forEach(s -> seatsByPax.put(((Number) s.get("paxId")).longValue(), s));
            request.getSeats().forEach(s -> seatsByPax.put(s.getPaxId(), seat(s.getPaxId(), s.getCoachName(), s.getSeatNumber())));
            train.setSeats(new ArrayList<>(seatsByPax.values()));
        }

        train.setStatus(BookingTrainStatus.BOOKED);
        train.setUpdatedBy(callerContext.userId());
        bookingTrainRepository.save(train);
        saveHistory(train, BookingTrainStatus.BOOKED, Map.of("seats", train.getSeats() != null ? train.getSeats() : List.of()));

        handleApprovalFlow(callerContext, booking, train);

        return bookingService.toResponse(booking, true, false);
    }

    private void handleApprovalFlow(CallerContext callerContext, Booking booking, BookingTrain train) {
        boolean autoApprove = companyConfigRepository.findByCompanyIdAndCode(callerContext.companyId(), "IS_AUTO_APPROVE_BOOKING_TRAIN")
            .map(c -> Boolean.TRUE.equals(c.getValueBool()))
            .orElse(false);

        // ponytail: no Appricode ping for train yet, approvers see it in GET /need-approval; add it when AppricodeService takes trains
        if (!autoApprove) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        train.setStatus(BookingTrainStatus.APPROVED);
        train.setApprovedBy(0L);
        train.setApprovedByEmail("SYSTEM");
        train.setApprovedAt(now);
        bookingTrainRepository.save(train);

        bookingApprovalRepository.saveAll(
            bookingCommonService.buildApprovals(
                booking.getId(),
                BookingType.TRAIN,
                List.of(train.getId()),
                ApprovalAction.APPROVED,
                0L,
                "SYSTEM",
                "Auto approve"
            )
        );

        saveHistory(train, BookingTrainStatus.APPROVED, Map.of(
            "approvedBy", 0,
            "approvedByEmail", "SYSTEM",
            "approvedAt", now.toString()
        ));

        bookingPubSubPublisher.publishBookingStatus(approvedMessage(booking, train.getBookingCode()));
    }

    // Called by the consumer (Pub/Sub APPROVED) and RetryApproved job: debit, then issue at Biztrip
    public void approveProcessBooking(CallerContext callerContext, Booking booking, BookingStatusMessage request) {
        BookingTrain train = bookingTrainRepository.findFirstByBookingIdAndBookingCode(request.getBookingId(), request.getBookingCode())
            .filter(t -> t.getStatus() == BookingTrainStatus.APPROVED)
            .orElse(null);
        if (train == null) {
            log.info("Train booking {} is not APPROVED anymore, skip issuing", request.getBookingCode());
            return;
        }

        if (isPastTimeLimit(train)) {
            cancel(train, "Time limit exceeded before issuing");
            return;
        }

        if (train.getTotalAmount() == null || train.getTotalAmount().signum() <= 0) {
            cancel(train, "Train booking has no total amount");
            return;
        }

        if (train.getPaymentMethod() == BookingPaymentMethod.LIMIT) {
            bookingCommonService.bookingDebitCredit(callerContext, booking, train);
        } else {
            bookingCommonService.bookingDebitBalance(callerContext, booking, train);
        }

        train.setStatus(BookingTrainStatus.ISSUING);
        bookingTrainRepository.save(train);

        JsonNode response;
        try {
            response = biztripTrainBookingService.issue(callerContext, train.getBookingReference());
        } catch (Exception e) {
            // Unknown outcome: keep ISSUING + debit, RetryIssuing job settles it from /train/booking/detail
            log.error("Train issue call failed for {}, left ISSUING", train.getBookingCode(), e);
            return;
        }
        saveHistory(train, BookingTrainStatus.ISSUING, response);

        if (!"OK".equalsIgnoreCase(response.path("TicketConfirmStatus").asText())) {
            String error = response.path("Error").asText();
            cancelAndRollback(callerContext, booking, train, error.isBlank() ? "Train issue failed: " + response : error);
            return;
        }

        try {
            applyPartnerStatus(callerContext, booking, train, biztripTrainBookingService.detail(callerContext, train.getBookingReference()));
        } catch (Exception e) {
            // Issue already accepted: must not roll back to APPROVED (would re-debit/re-issue); RetryIssuing job polls detail
            log.error("Train detail after issue failed for {}, left ISSUING", train.getBookingCode(), e);
        }
    }

    @Transactional
    public void retryApproveProcessBooking(CallerContext callerContext, Long bookingId, String bookingCode) {
        Booking booking = bookingCommonService.getValidatedBookingById(callerContext, true, bookingId);
        approveProcessBooking(callerContext, booking, approvedMessage(booking, bookingCode));
    }

    @Transactional
    public void retryIssuingProcessBooking(CallerContext callerContext, Long bookingId, String bookingCode) {
        Booking booking = bookingCommonService.getValidatedBookingById(callerContext, true, bookingId);
        BookingTrain train = bookingTrainRepository.findFirstByBookingIdAndBookingCode(bookingId, bookingCode)
            .filter(t -> t.getStatus() == BookingTrainStatus.ISSUING)
            .orElse(null);
        if (train == null) {
            return;
        }

        applyPartnerStatus(callerContext, booking, train, biztripTrainBookingService.detail(callerContext, train.getBookingReference()));

        if (train.getStatus() == BookingTrainStatus.ISSUING
            && train.getTimeLimit() != null
            && nowWib().isAfter(train.getTimeLimit().plusMinutes(ISSUING_GRACE_MINUTES))) {
            cancelAndRollback(callerContext, booking, train, "Not ticketed before time limit");
        }
    }

    @Transactional
    public void expire(Long trainId) {
        bookingTrainRepository.findById(trainId)
            .filter(t -> OPEN_STATUSES.contains(t.getStatus()) && isPastTimeLimit(t))
            .ifPresent(t -> cancel(t, "Time limit exceeded"));
    }

    public List<BookingTrain> findByStatus(BookingTrainStatus status) {
        return bookingTrainRepository.findTop100ByStatusOrderByIdAsc(status);
    }

    public List<BookingTrain> findExpired() {
        return bookingTrainRepository.findTop100ByStatusInAndTimeLimitBeforeOrderByIdAsc(OPEN_STATUSES, nowWib());
    }

    public static boolean isPastTimeLimit(BookingTrain train) {
        return train.getTimeLimit() != null && nowWib().isAfter(train.getTimeLimit());
    }

    private static LocalDateTime nowWib() {
        return LocalDateTime.now(BIZTRIP_ZONE);
    }

    private void applyPartnerStatus(CallerContext callerContext, Booking booking, BookingTrain train, BiztripTrainBookingData detail) {
        BookingTrainStatus next = BookingTrainStatus.fromPartnerStatus(detail.getStatus(), train.getStatus());
        if (next == BookingTrainStatus.ISSUED) {
            train.setStatus(BookingTrainStatus.ISSUED);
            bookingTrainRepository.save(train);
            saveHistory(train, BookingTrainStatus.ISSUED, detail);
        } else if (next == BookingTrainStatus.CANCELLED) {
            cancelAndRollback(callerContext, booking, train, "Biztrip status: " + detail.getStatus());
        }
    }

    private void cancelAndRollback(CallerContext callerContext, Booking booking, BookingTrain train, String reason) {
        log.warn("Train booking {} cancelled: {}", train.getBookingCode(), reason);
        cancel(train, reason);
        if (train.getPaymentMethod() == BookingPaymentMethod.LIMIT) {
            bookingCommonService.bookingRollbackCredit(callerContext, booking, train.getBookingCode());
        } else {
            bookingCommonService.bookingRollbackBalance(callerContext, booking, train.getBookingCode());
        }
    }

    private void cancel(BookingTrain train, String reason) {
        train.setStatus(BookingTrainStatus.CANCELLED);
        train.setErrorMessage(reason);
        bookingTrainRepository.save(train);
        saveHistory(train, BookingTrainStatus.CANCELLED, Map.of("reason", reason));
    }

    private BookingTrain getOpenTrain(Long bookingId, Long trainId) {
        BookingTrain train = bookingTrainRepository.findByIdAndBookingId(trainId, bookingId)
            .orElseThrow(() -> new NotFoundException("Booking train not found"));
        if (!OPEN_STATUSES.contains(train.getStatus())) {
            throw new IllegalStateException("Booking train is " + train.getStatus());
        }
        if (isPastTimeLimit(train)) {
            throw new IllegalStateException("Train booking time limit has passed");
        }
        return train;
    }

    private BookingStatusMessage approvedMessage(Booking booking, String bookingCode) {
        return BookingStatusMessage.builder()
            .companyId(booking.getCompanyId())
            .bookingType(BookingType.TRAIN)
            .bookingId(booking.getId())
            .bookingCode(bookingCode)
            .status(BookingStatus.APPROVED)
            .build();
    }

    private void saveHistory(BookingTrain train, BookingTrainStatus status, Object data) {
        bookingTrainHistoryRepository.save(
            BookingTrainHistory.builder()
                .bookingId(train.getBookingId())
                .bookingCode(train.getBookingCode())
                .status(status)
                .data(data)
                .build()
        );
    }

    private Map<String, Object> buildBookRequest(Booking booking, BookingTrain train, List<BookingPax> paxs) {
        String phone = localPhone(booking.getContactPhoneCode(), booking.getContactPhoneNumber());

        Map<String, Object> contact = new LinkedHashMap<>();
        contact.put("title", booking.getContactTitle());
        contact.put("firstName", booking.getContactFirstName());
        contact.put("lastName", booking.getContactLastName());
        contact.put("mobilePhone", phone);
        contact.put("homePhone", phone);
        contact.put("workPhone", phone);
        contact.put("otherPhone", "");
        contact.put("email", booking.getContactEmail());
        contact.put("address", "");
        contact.put("city", "");
        contact.put("provinceState", "");
        contact.put("postalCode", "");

        List<Map<String, Object>> passengers = new ArrayList<>();
        for (int i = 0; i < paxs.size(); i++) {
            BookingPax pax = paxs.get(i);
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("firstName", pax.getFirstName());
            p.put("lastName", pax.getLastName());
            p.put("title", pax.getTitle() != null ? pax.getTitle().name() : null);
            p.put("mobilePhone", localPhone(pax.getPhoneCode(), pax.getPhoneNumber()));
            p.put("email", pax.getEmail());
            p.put("identityType", pax.getDocumentType() != null ? pax.getDocumentType().toLowerCase() : null);
            p.put("identityNumber", pax.getDocumentNo());
            p.put("passportExpire", pax.getExpirationDate() != null ? pax.getExpirationDate().toString() : null);
            p.put("passportOrigin", pax.getIssuingCountry());
            p.put("birthDate", pax.getDob() != null ? pax.getDob().toString() : null);
            p.put("paxType", pax.getType() == PaxType.INFANT ? "INF" : pax.getType() == PaxType.CHILD ? "CHD" : "ADT");
            p.put("sequence", i + 1);
            passengers.add(p);
        }

        Map<String, Object> segment = new LinkedHashMap<>();
        segment.put("classKey", train.getClassKey());
        segment.put("carrierNumber", train.getCarrierNumber());
        segment.put("origin", train.getOrigin());
        segment.put("destination", train.getDestination());
        segment.put("departureTime", train.getDepartureDatetime().format(TIME));
        segment.put("departureDate", train.getDepartureDatetime().format(DATE));
        segment.put("arrivalTime", train.getArrivalDatetime().format(TIME));
        segment.put("arrivalDate", train.getArrivalDatetime().format(DATE));
        segment.put("journeyCode", train.getJourneyCode());
        segment.put("subClass", train.getSubClass());
        segment.put("class", train.getSeatClass() != null ? train.getSeatClass() : "-");
        segment.put("provider", train.getProvider());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("partnerBookingId", train.getBookingCode());
        body.put("contact", contact);
        body.put("passengers", passengers);
        body.put("journey", Map.of("segments", List.of(segment)));
        return body;
    }

    // Biztrip passenger index == the 1-based sequence we sent; seatNumber is "COACH/SEAT"
    private List<Map<String, Object>> toSeats(BiztripTrainBookingData data, List<BookingPax> paxs) {
        if (data.getPassengers() == null) {
            return List.of();
        }
        return data.getPassengers().stream()
            .filter(p -> p.getIndex() != null && p.getIndex() >= 1 && p.getIndex() <= paxs.size())
            .filter(p -> p.getSeatNumber() != null && p.getSeatNumber().contains("/"))
            .map(p -> {
                String[] parts = p.getSeatNumber().split("/", 2);
                return seat(paxs.get(p.getIndex() - 1).getId(), parts[0], parts[1]);
            })
            .toList();
    }

    private static Map<String, Object> seat(Long paxId, String coachName, String seatNumber) {
        Map<String, Object> seat = new LinkedHashMap<>();
        seat.put("paxId", paxId);
        seat.put("coachName", coachName);
        seat.put("seatNumber", seatNumber);
        return seat;
    }

    // Biztrip train wants local format: "08xx", not "+628xx" / "628xx"
    private static String localPhone(String phoneCode, String phoneNumber) {
        if (phoneNumber == null) {
            return "";
        }
        String number = phoneNumber.replaceAll("[^0-9]", "");
        String code = phoneCode != null ? phoneCode.replaceAll("[^0-9]", "") : "";
        if (!code.isEmpty() && number.startsWith(code)) {
            number = number.substring(code.length());
        }
        return number.startsWith("0") ? number : "0" + number;
    }
}
