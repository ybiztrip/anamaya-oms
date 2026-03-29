package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.queue.BookingPubSubPublisher;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.pubsub.BookingStatusMessage;
import ai.anamaya.service.oms.core.dto.request.BookingHotelListFilter;
import ai.anamaya.service.oms.core.dto.request.BookingHotelRequest;
import ai.anamaya.service.oms.core.dto.request.BookingHotelSubmitRequest;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingCheckRateRequest;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingCreateRequest;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingGetDetailRequest;
import ai.anamaya.service.oms.core.dto.response.BookingHotelResponse;
import ai.anamaya.service.oms.core.dto.response.BookingResponse;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCheckRateResponse;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCreateResponse;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingDetailResponse;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.entity.BookingPax;
import ai.anamaya.service.oms.core.entity.CompanyConfig;
import ai.anamaya.service.oms.core.enums.*;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.repository.BookingHotelRepository;
import ai.anamaya.service.oms.core.repository.BookingPaxRepository;
import ai.anamaya.service.oms.core.repository.CompanyConfigRepository;
import ai.anamaya.service.oms.core.specification.BookingHotelSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingHotelService {

    private final BookingHotelRepository bookingHotelRepository;
    private final BookingPaxRepository bookingPaxRepository;
    private final CompanyConfigRepository companyConfigRepository;
    private final BookingService bookingService;
    private final BookingCommonService bookingCommonService;
    private final BookingPaxService bookingPaxService;
    private final BookingPubSubPublisher bookingPubSubPublisher;

    private final Map<String, HotelProvider> hotelProviders;
    private HotelProvider getHotelProvider(String source) {
        String key = (source != null ? source.toLowerCase() : "biztrip") + "HotelProvider";
        HotelProvider provider = hotelProviders.get(key);

        if (provider == null) {
            log.warn("Provider '{}' not found, fallback to 'biztripHotelProvider'", key);
            provider = hotelProviders.get("biztripHotelProvider");
        }

        return provider;
    }

    public Page<BookingHotelResponse> getAll(int page, int size, String sort, BookingHotelListFilter filter) {

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

        Specification<BookingHotel> spec = BookingHotelSpecification.filter(filter);

        Page<BookingHotel> bookingHotels = bookingHotelRepository.findAll(spec, pageable);

        List<BookingHotelResponse> mapped = bookingHotels.getContent().stream()
            .map(this::toHotelResponse)
            .toList();

        return new PageImpl<>(mapped, pageable, bookingHotels.getTotalElements());
    }

    @Transactional
    public BookingResponse submitBookingHotel(CallerContext callerContext, Long bookingId, BookingHotelSubmitRequest request) {
        Long userId = callerContext.userId();
        Long companyId = callerContext.companyId();
        Booking booking = bookingService.getValidatedBooking(callerContext, bookingId);

        if (!booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AccessDeniedException("This booking journey is not approved.");
        }

        if(!bookingCommonService.validateBookingPaymentMethod(callerContext, request.getHotel().getPaymentMethod())) {
            throw new IllegalArgumentException("Invalid payment method");
        }

        BookingHotelRequest reqHotel = request.getHotel();
        String bookingCode = "ANMH:"+Instant.now().toEpochMilli();

        if (reqHotel.getCheckInDate().isBefore(booking.getStartDate())
            || reqHotel.getCheckInDate().isAfter(booking.getEndDate())
        ) {
            throw new IllegalArgumentException("Booking date hotel is outside journey date");
        }

        BookingHotelStatus status = BookingHotelStatus.BOOKED;
        Optional<CompanyConfig> isAutoApproveBookingHotel = companyConfigRepository.findByCompanyIdAndCode(companyId, "IS_AUTO_APPROVE_BOOKING_HOTEL");
        BookingStatusMessage message = null;
        if (isAutoApproveBookingHotel.isPresent() && Boolean.TRUE.equals(isAutoApproveBookingHotel.get().getValueBool())) {
            message = BookingStatusMessage.builder()
                .companyId(booking.getCompanyId())
                .bookingType(BookingType.HOTEL)
                .bookingId(booking.getId())
                .bookingCode(bookingCode)
                .status(BookingStatus.APPROVED)
                .build();
            status = BookingHotelStatus.APPROVED;
        }

        BookingHotel newHotel = BookingHotel.builder()
            .companyId(booking.getCompanyId())
            .bookingId(bookingId)
            .bookingCode(bookingCode)
            .clientSource(reqHotel.getClientSource())
            .itemId(reqHotel.getItemId())
            .roomId(reqHotel.getRoomId())
            .rateKey(reqHotel.getRateKey())
            .paymentMethod(reqHotel.getPaymentMethod())
            .paymentReference1(reqHotel.getPaymentReference1())
            .paymentReference2(reqHotel.getPaymentReference2())
            .numRoom(reqHotel.getNumRoom())
            .checkInDate(reqHotel.getCheckInDate())
            .checkOutDate(reqHotel.getCheckOutDate())
            .partnerSellAmount(reqHotel.getPartnerSellAmount())
            .partnerNettAmount(reqHotel.getPartnerNettAmount())
            .currency(reqHotel.getCurrency())
            .specialRequest(reqHotel.getSpecialRequest())
            .status(status)
            .createdBy(userId)
            .updatedBy(userId)
            .build();
        bookingHotelRepository.save(newHotel);

        bookingPaxService.submitBookingPax(callerContext, bookingId, bookingCode, request.getPaxs());

        if (message != null) {
            bookingPubSubPublisher.publishBookingStatus(message);
        } else {
            bookingCommonService.sendNotificationToApprover(callerContext, bookingId,null, List.of(newHotel));
        }

        return bookingService.toResponse(booking, true, false);
    }

    public void approveProcessBooking(CallerContext callerContext, Booking booking, BookingStatusMessage request) {
        List<BookingPax> bookingPaxes = bookingPaxRepository.findByBookingIdAndBookingCode(request.getBookingId(), request.getBookingCode());
        List<BookingHotel> bookingHotels = bookingHotelRepository.findByBookingIdAndBookingCode(request.getBookingId(), request.getBookingCode());
        processHotels(callerContext, booking, bookingPaxes, bookingHotels);

        bookingHotelRepository.saveAll(bookingHotels);
    }

    @Transactional
    public void retryApproveProcessBooking(CallerContext callerContext, Long bookingId, String bookingCode) {
        Booking booking = bookingCommonService.getValidatedBookingById(callerContext, true, bookingId);

        BookingStatusMessage statusMessage = BookingStatusMessage.builder()
            .companyId(booking.getCompanyId())
            .bookingType(BookingType.HOTEL)
            .bookingId(booking.getId())
            .bookingCode(bookingCode)
            .status(BookingStatus.APPROVED)
            .build();

        approveProcessBooking(callerContext, booking, statusMessage);
    }

    private void processHotels(
        CallerContext callerContext,
        Booking booking,
        List<BookingPax> bookingPaxes,
        List<BookingHotel> bookingHotels
    ) {
        HotelProvider provider = getHotelProvider("biztrip");

        for (BookingHotel hotel : bookingHotels) {

            HotelBookingDetailResponse bookingDetail = provider.getBookingDetail(callerContext, HotelBookingGetDetailRequest
                .builder()
                    .bookingId(hotel.getBookingReference())
                    .partnerBookingId(hotel.getBookingCode())
                .build());

            BookingHotelStatus hotelStatus =
                BookingHotelStatus.fromBookingPartnerStatus(
                    bookingDetail.getStatus()
                );

            if(hotelStatus == BookingHotelStatus.ISSUED) {
                hotel.setPartnerNettAmount(Double.valueOf(bookingDetail.getTotalAmount()));
                hotel.setPartnerSellAmount(Double.valueOf(bookingDetail.getTotalAmount()));
                hotel.setStatus(hotelStatus);
                bookingCommonService.bookingDebitBalance(callerContext, booking, null, hotel);
                continue;
            }

            if(hotelStatus == BookingHotelStatus.CANCELLED) {
                hotel.setStatus(hotelStatus);
                bookingCommonService.bookingRollbackBalance(callerContext, booking, null, hotel);
                continue;
            }

            HotelBookingCheckRateResponse rateResponse =
                provider.checkRate(
                    callerContext,
                    buildHotelBookingCheckRateRequest(hotel, bookingPaxes)
                );

            if(rateResponse.getIsCancel()) {
                hotel.setStatus(BookingHotelStatus.CANCELLED);
                bookingCommonService.bookingRollbackBalance(callerContext, booking, null, hotel);
                continue;
            }

            if (!"AVAILABLE".equals(rateResponse.getRateStatus())) {
                throw new IllegalStateException(
                    "Hotel rate not available for bookingId=" + booking.getId()
                );
            }

            hotel.setPartnerNettAmount(Double.valueOf(rateResponse.getNettAmount()));
            hotel.setPartnerSellAmount(Double.valueOf(rateResponse.getSellAmount()));
            hotel.setPaymentKey(rateResponse.getPaymentKey());

            if (hotel.getPaymentMethod() == BookingPaymentMethod.DEPOSIT) {
                bookingCommonService.bookingDebitBalance(callerContext, booking, null, hotel);
            }

            HotelBookingCreateResponse createResponse =
                provider.create(
                    callerContext,
                    buildHotelBookingCreateRequest(booking, bookingPaxes, hotel)
                );

            if (Boolean.TRUE.equals(createResponse.getIsCancel())) {
                hotel.setStatus(BookingHotelStatus.CANCELLED);
                bookingCommonService.bookingRollbackBalance(callerContext, booking, null, hotel);
                continue;
            }

            hotelStatus = BookingHotelStatus.fromBookingPartnerStatus(
                createResponse.getStatus()
            );

            hotel.setStatus(hotelStatus);
            hotel.setBookingReference(createResponse.getBookingReference());
            hotel.setPaymentUrl(createResponse.getPaymentUrl());
            hotel.setPartnerSellAmount(
                Double.valueOf(createResponse.getTotalAmount())
            );
            hotel.setCurrency(createResponse.getCurrency());
        }
    }

    private HotelBookingCheckRateRequest buildHotelBookingCheckRateRequest(
        BookingHotel hotel,
        List<BookingPax> bookingPaxes
    ) {

        long numAdults =
            bookingPaxes.stream()
                .filter(p -> p.getType() == PaxType.ADULT)
                .count();

        return HotelBookingCheckRateRequest.builder()
            .propertyId(hotel.getItemId())
            .roomId(hotel.getRoomId())
            .checkInDate(hotel.getCheckInDate().toString())
            .checkOutDate(hotel.getCheckOutDate().toString())
            .numRooms(hotel.getNumRoom().intValue())
            .numAdults((int) numAdults)
            .displayCurrency(hotel.getCurrency())
            .userNationality("ID")
            .rateKey(hotel.getRateKey())
            .build();
    }

    private HotelBookingCreateRequest buildHotelBookingCreateRequest(
        Booking booking,
        List<BookingPax> bookingPaxes,
        BookingHotel hotel
    ) {

        // --- Pre-calculate commonly used values ---
        BookingPaymentMethod paymentMethod = hotel.getPaymentMethod();
        if (paymentMethod == null) {
            paymentMethod = BookingPaymentMethod.DEPOSIT;
        }

        List<BookingPax> adultPaxes = bookingPaxes.stream()
            .filter(p -> p.getType() == PaxType.ADULT)
            .toList();

        List<HotelBookingCreateRequest.GuestInfo> guests = adultPaxes.stream()
            .map(pax -> HotelBookingCreateRequest.GuestInfo.builder()
                .title(pax.getTitle() != null ? pax.getTitle().name() : null)
                .firstName(pax.getFirstName())
                .lastName(pax.getLastName())
                .email(pax.getEmail())
                .gender(pax.getGender() != null ? pax.getGender().name() : null)
                .idtype(pax.getDocumentType())
                .idnumber(pax.getDocumentNo())
                .build()
            )
            .toList();

        int numAdults = adultPaxes.size();

        List<Integer> childrenAges = bookingPaxes.stream()
            .filter(p -> p.getType() == PaxType.CHILD)
            .map(p -> p.getDob() != null
                ? LocalDate.now().getYear() - p.getDob().getYear()
                : null)
            .filter(Objects::nonNull)
            .toList();

        HotelBookingCreateRequest.UserPayment userPayment =
            HotelBookingCreateRequest.UserPayment.builder()
                .userPayment(paymentMethod)
                .build();

        if (paymentMethod == BookingPaymentMethod.CUST_CREDIT_CARD) {
            HotelBookingCreateRequest.CreditCardDetail creditCardDetail =
                new HotelBookingCreateRequest.CreditCardDetail();

            creditCardDetail.setCardName(hotel.getPaymentReference1());
            creditCardDetail.setLastSixDigitNumber(hotel.getPaymentReference2());

            userPayment.setCreditCardDetail(creditCardDetail);
        }

        // --- Build Request ---
        return HotelBookingCreateRequest.builder()
            .propertyId(hotel.getItemId())
            .partnerBookingId(hotel.getBookingCode())
            .checkInDate(hotel.getCheckInDate().toString())
            .checkOutDate(hotel.getCheckOutDate().toString())
            .displayCurrency(hotel.getCurrency())
            .specialRequest(hotel.getSpecialRequest())
            .language("en")
            .userNationality("ID")

            .customerInfo(
                HotelBookingCreateRequest.CustomerInfo.builder()
                    .title(booking.getContactTitle() != null
                        ? booking.getContactFirstName() + " " + booking.getContactLastName()
                        : null)
                    .firstName(booking.getContactFirstName())
                    .lastName(booking.getContactLastName())
                    .email(booking.getContactEmail())
                    .phone(booking.getContactPhoneNumber())
                    .build()
            )

            .rooms(List.of(
                HotelBookingCreateRequest.Room.builder()
                    .roomId(hotel.getRoomId())
                    .rateKey(hotel.getRateKey())
                    .paymentKey(hotel.getPaymentKey())
                    .numRooms(hotel.getNumRoom().intValue())
                    .numAdults(numAdults)
                    .numChild(childrenAges.size())
                    .childrenAges(childrenAges)
                    .guestInfo(guests)
                    .build()
            ))

            .totalRates(
                HotelBookingCreateRequest.TotalRates.builder()
                    .partnerSellAmount(String.valueOf(hotel.getPartnerSellAmount()))
                    .partnerNettAmount(String.valueOf(hotel.getPartnerNettAmount()))
                    .build()
            )

            .userPayment(userPayment)

            .additionalData(
                booking.getAdditionalInfo() != null
                    ? booking.getAdditionalInfo().toString()
                    : "{}"
            )
            .build();
    }

    private BookingHotelResponse toHotelResponse(BookingHotel h) {
        return BookingHotelResponse.builder()
            .id(h.getId())
            .companyId(h.getCompanyId())
            .bookingId(h.getBookingId())
            .bookingCode(h.getBookingCode())
            .clientSource(h.getClientSource())
            .itemId(h.getItemId())
            .roomId(h.getRoomId())
            .rateKey(h.getRateKey())
            .numRoom(h.getNumRoom())
            .checkInDate(h.getCheckInDate())
            .checkOutDate(h.getCheckOutDate())
            .partnerSellAmount(h.getPartnerSellAmount())
            .partnerNettAmount(h.getPartnerNettAmount())
            .currency(h.getCurrency())
            .specialRequest(h.getSpecialRequest())
            .status(h.getStatus())
            .build();
    }
}
