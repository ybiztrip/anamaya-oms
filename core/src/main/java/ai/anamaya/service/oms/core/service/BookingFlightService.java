package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.queue.BookingPubSubPublisher;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.pubsub.BookingStatusMessage;
import ai.anamaya.service.oms.core.dto.request.*;
import ai.anamaya.service.oms.core.dto.request.booking.payment.FlightBookingPaymentRequest;
import ai.anamaya.service.oms.core.dto.request.booking.status.FlightBookingStatusCheckRequest;
import ai.anamaya.service.oms.core.dto.request.booking.submit.*;
import ai.anamaya.service.oms.core.dto.response.BookingFlightResponse;
import ai.anamaya.service.oms.core.dto.response.BookingResponse;
import ai.anamaya.service.oms.core.dto.response.booking.data.BookingDataResponse;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingFlightSubmitResponse;
import ai.anamaya.service.oms.core.entity.*;
import ai.anamaya.service.oms.core.enums.*;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.helper.json.JsonHelper;
import ai.anamaya.service.oms.core.repository.*;
import ai.anamaya.service.oms.core.specification.BookingFlightSpecification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingFlightService {

    private final BookingApprovalRepository bookingApprovalRepository;
    private final BookingCommonService bookingCommonService;
    private final BookingFlightRepository bookingFlightRepository;
    private final BookingFlightHistoryRepository bookingFlightHistoryRepository;
    private final BookingPaxRepository bookingPaxRepository;
    private final BookingService bookingService;
    private final BookingPaxService bookingPaxService;
    private final BookingPubSubPublisher bookingPubSubPublisher;
    private final CompanyConfigRepository companyConfigRepository;
    private final JsonHelper jsonHelper;
    private final Map<String, FlightProvider> flightProviders;
    private final ObjectMapper mapper = new ObjectMapper();

    private FlightProvider getProvider(String source) {
        String key = (source != null ? source.toLowerCase() : "biztrip") + "FlightProvider";
        FlightProvider provider = flightProviders.get(key);

        if (provider == null) {
            log.warn("Provider '{}' not found, fallback to 'biztripFlightProvider'", key);
            provider = flightProviders.get("biztripFlightProvider");
        }

        return provider;
    }

    public Page<BookingFlightResponse> getAll(int page, int size, String sort, BookingFlightListFilter filter) {

        // Sorting
        Sort sorting = Sort.by("createdAt").descending();

        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(";");
            String field = parts[0];

            Sort.Direction direction =
                (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            sorting = Sort.by(direction, field);
        }

        Pageable pageable = PageRequest.of(page, size, sorting);

        Specification<BookingFlight> spec = BookingFlightSpecification.filter(filter);

        Page<BookingFlight> bookingFlights = bookingFlightRepository.findAll(spec, pageable);

        List<BookingFlightResponse> mapped = bookingFlights.getContent().stream()
            .map(this::toResponse)
            .toList();

        return new PageImpl<>(mapped, pageable, bookingFlights.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public BookingResponse submitBookingFlights(CallerContext callerContext, Long bookingId, BookingFlightSubmitRequest request) {
        Long userId = callerContext.userId();
        Booking booking = bookingService.getValidatedBooking(callerContext, bookingId);

        if (!booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AccessDeniedException("This booking journey is not approved.");
        }

        if(!bookingCommonService.validateBookingPaymentMethod(callerContext, request.getFlights().get(0).getPaymentMethod())) {
            throw new IllegalArgumentException("Invalid payment method");
        }

        List<BookingFlightRequest> requestFlights = request.getFlights();
        String bookingCode = "ANMF:"+ Instant.now().toEpochMilli();

        for (BookingFlightRequest req : requestFlights) {
            LocalDate departureDate = req.getDepartureDatetime().toLocalDate();
            if (departureDate.isBefore(booking.getStartDate())
                || departureDate.isAfter(booking.getEndDate())
            ) {
                throw new IllegalArgumentException("Booking date flight is outside journey date");
            }
            BookingFlight newFlight = BookingFlight.builder()
                .companyId(booking.getCompanyId())
                .bookingId(bookingId)
                .bookingCode(bookingCode)
                .type(req.getType())
                .clientSource(req.getClientSource())
                .itemId(req.getItemId())
                .origin(req.getOrigin())
                .destination(req.getDestination())
                .departureDatetime(req.getDepartureDatetime())
                .arrivalDatetime(req.getArrivalDatetime())
                .paymentMethod(req.getPaymentMethod())
                .paymentReference1(req.getPaymentReference1())
                .paymentReference2(req.getPaymentReference2())
                .status(BookingFlightStatus.CREATED)
                .metadata(jsonHelper.toJson(req.getMetadata()))
                .createdBy(userId)
                .updatedBy(userId)
                .build();
            bookingFlightRepository.save(newFlight);
        }

        bookingPaxService.submitBookingPax(callerContext, bookingId, bookingCode, request.getPaxs());

        createBookingFlight(callerContext, booking, bookingCode);
        handleBookingFlightApprovalFlow(callerContext, booking, bookingCode);
        bookingCommonService.bookingTravelPolicy(callerContext, bookingCode, BookingType.FLIGHT);

        return bookingService.toResponse(booking, true, false);
    }

    public void handleBookingFlightApprovalFlow(CallerContext callerContext, Booking booking, String bookingCode) {
        Optional<CompanyConfig> isAutoApproveBookingFlight = companyConfigRepository.findByCompanyIdAndCode(callerContext.companyId(), "IS_AUTO_APPROVE_BOOKING_FLIGHT");
        BookingStatusMessage message = null;

        if (isAutoApproveBookingFlight.isPresent() && Boolean.TRUE.equals(isAutoApproveBookingFlight.get().getValueBool())) {
            message = BookingStatusMessage.builder()
                .companyId(booking.getCompanyId())
                .bookingType(BookingType.FLIGHT)
                .bookingId(booking.getId())
                .bookingCode(bookingCode)
                .status(BookingStatus.APPROVED)
                .build();
        }

        List<BookingFlight> bookingFlights = bookingFlightRepository.findByBookingIdAndBookingCode(booking.getId(), bookingCode);
        if (message != null) {
            bookingApprovalRepository.saveAll(
                bookingCommonService.buildApprovals(
                    booking.getId(),
                    BookingType.FLIGHT,
                    bookingFlights.stream().map(BookingFlight::getId).toList(),
                    ApprovalAction.APPROVED,
                    0L,
                    "SYSTEM",
                    "Auto approve"
                )
            );
            bookingPubSubPublisher.publishBookingStatus(message);
        } else {
            bookingCommonService.sendNotificationToApprover(callerContext, booking.getId() ,bookingFlights, null);
        }

    }

    public void approveProcessBooking(CallerContext callerContext, Booking booking, BookingStatusMessage request) {
        Long userId = callerContext.userId();

        List<BookingFlight> bookingFlights = bookingFlightRepository.findByBookingIdAndBookingCode(request.getBookingId(), request.getBookingCode());

        BookingFlightStatus flightStatus;
        if (bookingFlights.get(0).getPaymentMethod() == BookingPaymentMethod.DEPOSIT) {
            flightStatus = BookingFlightStatus.ISSUING;
            bookingCommonService.bookingDebitBalance(callerContext, booking, bookingFlights, null);
        } else if (bookingFlights.get(0).getPaymentMethod() == BookingPaymentMethod.CREDIT) {
            flightStatus = BookingFlightStatus.ISSUING;
            bookingCommonService.bookingDebitCredit(callerContext, booking, bookingFlights, null);
        } else {
            flightStatus = BookingFlightStatus.WAITING_PAYMENT;
        }
        BookingFlightSubmitResponse bookingFlightSubmitResponse = processFlights(callerContext, booking, bookingFlights);

        bookingFlights.forEach(h -> {
            h.setStatus(flightStatus);
            h.setPaymentUrl(bookingFlightSubmitResponse.getPaymentUrl());
            h.setUpdatedBy(userId);
        });
        bookingFlightRepository.saveAll(bookingFlights);
    }

    @Transactional
    public void retryApproveProcessBooking(CallerContext callerContext, Long bookingId, String bookingCode) {
        Booking booking = bookingCommonService.getValidatedBookingById(callerContext, true, bookingId);

        BookingStatusMessage statusMessage = BookingStatusMessage.builder()
            .companyId(booking.getCompanyId())
            .bookingType(BookingType.FLIGHT)
            .bookingId(booking.getId())
            .bookingCode(bookingCode)
            .status(BookingStatus.APPROVED)
            .build();

        approveProcessBooking(callerContext, booking, statusMessage);
    }

    @Transactional
    public void retryIssuingProcessBooking(CallerContext callerContext, Long bookingId, String bookingCode) {
        Booking booking = bookingCommonService.getValidatedBookingById(callerContext, true, bookingId);
        List<BookingFlight> bookingFlights = bookingFlightRepository.findByBookingIdAndBookingCode(bookingId, bookingCode);
        List<String> bookingReferenceIds = bookingFlights.stream()
            .map(BookingFlight::getBookingReference)
            .filter(Objects::nonNull)
            .toList();
        if (bookingReferenceIds.isEmpty()) {
            throw new IllegalStateException("No booking reference found for bookingId: " + bookingId);
        }

        FlightProvider provider = getProvider("biztrip");
        BookingFlightSubmitResponse response = provider.checkStatus(
            callerContext,
            FlightBookingStatusCheckRequest
                .builder()
                .bookingReferenceIds(bookingReferenceIds)
                .build()
        );

        BookingFlightStatus status = BookingFlightStatus.fromBookingPartnerStatus(response.getBookingSubmissionStatus());
        if(status == BookingFlightStatus.ISSUED) {
            bookingFlightRepository.updateStatusByBookingReferences(bookingId, bookingReferenceIds, BookingFlightStatus.ISSUED);
        }

        if(status == BookingFlightStatus.CANCELLED) {
            bookingFlightRepository.updateStatusByBookingReferences(bookingId, bookingReferenceIds, BookingFlightStatus.CANCELLED);
            if(bookingFlights.get(0).getPaymentMethod() == BookingPaymentMethod.DEPOSIT) {
                bookingCommonService.bookingRollbackBalance(callerContext, booking, bookingFlights, null);
            } else if(bookingFlights.get(0).getPaymentMethod() == BookingPaymentMethod.CREDIT) {
                bookingCommonService.bookingRollbackCredit(callerContext, booking, bookingFlights, null);
            }
        }
    }

    private BookingFlightSubmitResponse processFlights(
        CallerContext callerContext,
        Booking booking,
        List<BookingFlight> bookingFlights
    ) {
        FlightProvider provider = getProvider("biztrip");
        BookingPaymentMethod paymentMethod = bookingFlights.get(0).getPaymentMethod();
        if(paymentMethod == null) {
            paymentMethod = BookingPaymentMethod.DEPOSIT;
        }

        FlightBookingPaymentRequest.FlightBookingPaymentRequestBuilder builder =
            FlightBookingPaymentRequest.builder()
                .bookingId(bookingFlights.get(0).getBookingReference())
                .paymentMethod(paymentMethod);

        if (paymentMethod == BookingPaymentMethod.CUST_CREDIT_CARD) {
            builder.creditCardDetail(
                FlightBookingPaymentRequest.CreditCardDetail.builder()
                    .cardName(bookingFlights.get(0).getPaymentReference1())
                    .lastSixDigitNumber(bookingFlights.get(0).getPaymentReference2())
                    .build()
            );
        }

        BookingFlightSubmitResponse response = provider.payment(
            callerContext,
            builder.build()
        );

        BookingFlightStatus flightStatus =
            BookingFlightStatus.fromPaymentPartnerStatus(
                response.getBookingSubmissionStatus()
            );

        bookingFlightHistoryRepository.save(
            BookingFlightHistory.builder()
                .bookingId(booking.getId())
                .bookingCode(bookingFlights.get(0).getBookingCode())
                .status(flightStatus)
                .data(response)
                .build()
        );

        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public void retryBookingCreatedFlights(CallerContext callerContext, Long bookingFlightId) {
        Optional<BookingFlight> bookingFlightData = bookingFlightRepository.findById(bookingFlightId);
        if (bookingFlightData.isEmpty()){
            return;
        }

        BookingFlight bookingFlight = bookingFlightData.get();
        if (!bookingFlight.getStatus().equals(BookingFlightStatus.CREATED)) {
            return;
        }

        Booking booking = bookingService.getValidatedBooking(callerContext, bookingFlight.getBookingId());
        if (!booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.info("[retryBookingCreatedFlights] This booking journey is not approved.");
            return;
        }

        FlightProvider provider = getProvider("biztrip");
        List<BookingDataResponse> bookingDataResponse = provider.searchData(callerContext, FlightBookingSearchDataRequest.builder()
            .count(100)
            .page(0)
            .referenceCodes(List.of(bookingFlight.getBookingReference()))
            .build());

        updateBookingFlightData(booking.getId(), bookingFlight.getBookingCode(), bookingDataResponse);
        handleBookingFlightApprovalFlow(callerContext, booking, bookingFlight.getBookingCode());
    }

    private void createBookingFlight(CallerContext callerContext, Booking booking, String bookingCode) {
        List<BookingPax> pax = bookingPaxRepository.findByBookingIdAndBookingCode(booking.getId(), bookingCode);
        List<BookingFlight> flights = bookingFlightRepository.findByBookingIdAndBookingCode(booking.getId(), bookingCode);
        if(flights == null || flights.isEmpty()) {
            return;
        }

        if (!flights.get(0).getStatus().equals(BookingFlightStatus.CREATED)) {
            return;
        }

        FlightBookingSubmitRequest request = buildSubmitRequest(booking, pax, flights);

        FlightProvider provider = getProvider("biztrip");

        BookingFlightSubmitResponse response = provider.submitBooking(callerContext, request);
        BookingFlightStatus bookingFlightStatus = BookingFlightStatus.fromBookingPartnerStatus(response.getBookingSubmissionStatus());
        bookingFlightHistoryRepository.save(
            BookingFlightHistory.builder()
                .bookingId(booking.getId())
                .bookingCode(bookingCode)
                .status(bookingFlightStatus)
                .data(response)
                .build()
        );

        List<BookingDataResponse> bookingDataResponse = provider.searchData(callerContext, FlightBookingSearchDataRequest.builder()
            .count(100)
            .page(0)
            .referenceCodes(List.of(response.getBookingId()))
            .build());

        updateBookingFlightData(booking.getId(), bookingCode, bookingDataResponse);
    }

    private void updateBookingFlightData(Long bookingId, String bookingCode, List<BookingDataResponse> responses) {
        List<BookingFlight> flights = bookingFlightRepository.findByBookingIdAndBookingCode(bookingId, bookingCode);

        for (BookingFlight f : flights) {
            if (f.getStatus().equals(BookingFlightStatus.BOOKED)) {
                continue;
            }

            for (BookingDataResponse data : responses) {
                if (!data.getDeparture().equals(f.getOrigin()) && !data.getArrival().equals(f.getDestination()) ) {
                    continue;
                }

                if (data.getAdultPrice() != null
                    && data.getAdultPrice().compareTo(BigDecimal.ZERO) > 0) {
                    f.setAdultAmount(data.getAdultPrice());
                }

                if (data.getChildPrice() != null
                    && data.getChildPrice().compareTo(BigDecimal.ZERO) > 0) {
                    f.setChildAmount(data.getChildPrice());
                }

                if (data.getInfantPrice() != null
                    && data.getInfantPrice().compareTo(BigDecimal.ZERO) > 0) {
                    f.setInfantAmount(data.getInfantPrice());
                }

                if (data.getTotalPrice() != null
                    && data.getTotalPrice().compareTo(BigDecimal.ZERO) > 0) {
                    f.setTotalAmount(data.getTotalPrice());
                }

                BookingFlightStatus flightStatus = BookingFlightStatus.fromBookingPartnerStatus(data.getStatus());
                f.setStatus(flightStatus);
                f.setBookingReference(data.getBookingReference());
                f.setOtaReference(data.getOtaReference());

                bookingFlightRepository.save(f);
                if(!flightStatus.equals(BookingFlightStatus.BOOKED)) {
                    continue;
                }

                bookingFlightHistoryRepository.save(
                    BookingFlightHistory.builder()
                        .bookingId(bookingId)
                        .status(flightStatus)
                        .data(data.toString())
                        .build()
                );
            }
        }

    }

    private FlightBookingSubmitRequest buildSubmitRequest(
        Booking booking,
        List<BookingPax> paxList,
        List<BookingFlight> flightList
    ) {

        ContactDetail contact = ContactDetail.builder()
            .email(booking.getContactEmail())
            .firstName(booking.getContactFirstName())
            .lastName(booking.getContactLastName())
            .phoneNumber(booking.getContactPhoneNumber())
            .phoneNumberCountryCode(booking.getContactPhoneCode())
            .customerEmail(booking.getContactEmail())
            .customerPhoneNumber(booking.getContactPhoneNumber())
            .customerPhoneNumberCountryCode(booking.getContactPhoneCode())
            .title(booking.getContactTitle())
            .dateOfBirth(
                booking.getContactDob() != null
                    ? booking.getContactDob().toString()
                    : null
            )
            .build();

        List<Passenger> adults = paxList.stream()
            .filter(p -> p.getType() == PaxType.ADULT)
            .map(pax -> Passenger.builder()
                .title(pax.getTitle() != null ? pax.getTitle().name() : null)
                .firstName(pax.getFirstName())
                .lastName(pax.getLastName())
                .gender(pax.getGender() != null ? pax.getGender().name() : null)
                .dateOfBirth(pax.getDob() != null ? pax.getDob().toString() : null)
                .nationality(pax.getNationality())
                .documentDetail(new DocumentDetail(
                    pax.getIssuingCountry(),
                    pax.getDocumentNo(),
                    pax.getExpirationDate() != null ? pax.getExpirationDate().toString() : null,
                    pax.getDocumentType()
                ))
                .addOns(pax.getAddOn() != null ? mapper.convertValue(pax.getAddOn(), List.class) : null)
                .build()
            ).toList();


        return FlightBookingSubmitRequest.builder()
            .contactDetail(contact)
            .passengers(new Passengers(adults, null, null))
            .flightIds(flightList.stream().map(BookingFlight::getItemId).toList())
            .partnerBookingId(booking.getCode())
            .destinationId(booking.getJourneyCode())
            .journeyType(flightList.size() > 1 ? "ROUND_TRIP" : "ONE_WAY")
            .locale("id_ID")
            .loginID(booking.getContactEmail())
            .loginType("EMAIL")
            .customerLoginID(booking.getContactEmail())
            .customerLoginType("EMAIL")
            .source("GSP")
            .jabatan("")
            .additionalData(booking.getAdditionalInfo() != null
                ? booking.getAdditionalInfo().toString()
                : "{}")
            .build();
    }

    private BookingFlightResponse toResponse(BookingFlight f) {
        return BookingFlightResponse.builder()
            .id(f.getId())
            .companyId(f.getCompanyId())
            .bookingId(f.getBookingId())
            .bookingCode(f.getBookingCode())
            .type(f.getType())
            .clientSource(f.getClientSource())
            .itemId(f.getItemId())
            .origin(f.getOrigin())
            .destination(f.getDestination())
            .departureDatetime(f.getDepartureDatetime())
            .arrivalDatetime(f.getArrivalDatetime())
            .status(f.getStatus())
            .metadata(
                f.getMetadata() != null
                    ? jsonHelper.toJsonNode(f.getMetadata())
                    : jsonHelper.emptyObject()
            )
            .build();
    }

}
