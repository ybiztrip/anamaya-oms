package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.queue.BookingPubSubPublisher;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.pubsub.BookingStatusMessage;
import ai.anamaya.service.oms.core.dto.request.BookingHotelListFilter;
import ai.anamaya.service.oms.core.dto.request.BookingHotelRequest;
import ai.anamaya.service.oms.core.dto.request.BookingHotelSubmitRequest;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingCheckRateRequest;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingCreateRequest;
import ai.anamaya.service.oms.core.dto.response.BookingHotelResponse;
import ai.anamaya.service.oms.core.dto.response.BookingResponse;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCheckRateResponse;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCreateResponse;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.entity.BookingPax;
import ai.anamaya.service.oms.core.entity.CompanyConfig;
import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
import ai.anamaya.service.oms.core.enums.BookingStatus;
import ai.anamaya.service.oms.core.enums.BookingType;
import ai.anamaya.service.oms.core.enums.PaxType;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.repository.BookingHotelRepository;
import ai.anamaya.service.oms.core.repository.BookingPaxRepository;
import ai.anamaya.service.oms.core.repository.CompanyConfigRepository;
import jakarta.persistence.criteria.Predicate;
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

        Specification<BookingHotel> spec = BookingHotelService.BookingSpecification.filter(filter);

        Page<BookingHotel> bookingHotels = bookingHotelRepository.findAll(spec, pageable);

        List<BookingHotelResponse> mapped = bookingHotels.getContent().stream()
            .map(this::toHotelResponse)
            .toList();

        return new PageImpl<>(mapped, pageable, bookingHotels.getTotalElements());
    }

    public static class BookingSpecification {

        public static Specification<BookingHotel> filter(BookingHotelListFilter filter) {
            return (root, query, cb) -> {

                List<Predicate> predicates = new ArrayList<>();

                if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
                    predicates.add(root.get("status").in(filter.getStatuses()));
                }

                if (filter.getCompanyId() != null && filter.getCompanyId() != 0) {
                    predicates.add(cb.equal(root.get("companyId"), filter.getCompanyId()));
                }

                if (filter.getDateFrom() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        filter.getDateFrom().atStartOfDay()
                    ));
                }

                if (filter.getDateTo() != null) {
                    predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdAt"),
                        filter.getDateTo().atTime(23, 59, 59)
                    ));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            };
        }
    }

    @Transactional
    public BookingResponse submitBookingHotel(CallerContext callerContext, Long bookingId, BookingHotelSubmitRequest request) {
        Long userId = callerContext.userId();
        Long companyId = callerContext.companyId();
        Booking booking = bookingService.getValidatedBooking(callerContext, bookingId);

        if (!booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AccessDeniedException("This booking journey is not approved.");
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
        }

        return bookingService.toResponse(booking, true, true);
    }

    public void approveProcessBooking(CallerContext callerContext, Booking booking, BookingStatusMessage request) {
        List<BookingPax> bookingPaxes = bookingPaxRepository.findByBookingIdAndBookingCode(request.getBookingId(), request.getBookingCode());
        List<BookingHotel> bookingHotels = bookingHotelRepository.findByBookingIdAndBookingCode(request.getBookingId(), request.getBookingCode());
        processHotels(callerContext, booking, bookingPaxes, bookingHotels);

        bookingHotelRepository.saveAll(bookingHotels);
    }

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

            HotelBookingCheckRateResponse rateResponse =
                provider.checkRate(
                    callerContext,
                    buildHotelBookingCheckRateRequest(hotel, bookingPaxes)
                );

            if(rateResponse.getIsCancel()) {
                hotel.setStatus(BookingHotelStatus.CANCELLED);
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

            bookingCommonService.bookingDebitBalance(callerContext, booking, null, hotel);

            HotelBookingCreateResponse createResponse =
                provider.create(
                    callerContext,
                    buildHotelBookingCreateRequest(booking, bookingPaxes, hotel)
                );

            if(createResponse.getIsCancel()) {
                hotel.setStatus(BookingHotelStatus.CANCELLED);
                continue;
            }

            BookingHotelStatus hotelStatus =
                BookingHotelStatus.fromBookingPartnerStatus(
                    createResponse.getStatus()
                );

            hotel.setStatus(hotelStatus);
            hotel.setBookingReference(createResponse.getBookingReference());
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

        List<HotelBookingCreateRequest.GuestInfo> guests =
            bookingPaxes.stream()
                .filter(pax -> pax.getType() == PaxType.ADULT)
                .map(pax ->
                    HotelBookingCreateRequest.GuestInfo.builder()
                        .title(pax.getTitle() != null
                            ? pax.getTitle().name()
                            : null)
                        .firstName(pax.getFirstName())
                        .lastName(pax.getLastName())
                        .email(pax.getEmail())
                        .gender(pax.getGender() != null
                            ? pax.getGender().name()
                            : null)
                        .idtype(pax.getDocumentType())
                        .idnumber(pax.getDocumentNo())
                        .build()
                )
                .toList();

        long numAdults =
            bookingPaxes.stream()
                .filter(p -> p.getType() == PaxType.ADULT)
                .count();

        List<Integer> childrenAges =
            bookingPaxes.stream()
                .filter(p -> p.getType() == PaxType.CHILD)
                .map(p -> p.getDob() != null
                    ? LocalDate.now().getYear() - p.getDob().getYear()
                    : null)
                .filter(Objects::nonNull)
                .toList();

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
                        ? booking.getContactFirstName()+ " " + booking.getContactLastName()
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
                    .numAdults((int) numAdults)
                    .numChild(childrenAges.size())
                    .childrenAges(childrenAges)
                    .guestInfo(guests)
                    .build()
            ))

            .totalRates(
                HotelBookingCreateRequest.TotalRates.builder()
                    .partnerSellAmount(
                        String.valueOf(hotel.getPartnerSellAmount())
                    )
                    .partnerNettAmount(
                        String.valueOf(hotel.getPartnerNettAmount())
                    )
                    .build()
            )

            .userPayment(
                HotelBookingCreateRequest.UserPayment.builder()
                    .userPayment("DEPOSIT")
                    .build()
            )

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
