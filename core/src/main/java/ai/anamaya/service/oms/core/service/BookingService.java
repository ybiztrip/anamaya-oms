package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.BookingListFilter;
import ai.anamaya.service.oms.core.dto.request.BookingRequest;
import ai.anamaya.service.oms.core.dto.request.BookingUpdateStatusRequest;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.entity.*;
import ai.anamaya.service.oms.core.enums.ApprovalAction;
import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
import ai.anamaya.service.oms.core.enums.BookingStatus;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.helper.json.JsonHelper;
import ai.anamaya.service.oms.core.repository.*;
import ai.anamaya.service.oms.core.specification.BookingSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingApprovalRepository bookingApprovalRepository;
    private final BookingAttachmentRepository bookingAttachmentRepository;
    private final BookingRepository bookingRepository;
    private final BookingPaxRepository bookingPaxRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final BookingHotelRepository bookingHotelRepository;
    private final CompanyConfigRepository companyConfigRepository;
    private final JsonHelper jsonHelper;

    public Page<BookingResponse> getAll(BookingListFilter filter) {

        // Sorting
        Sort sorting = Sort.by("createdAt").descending();

        if (filter.getSort() != null && !filter.getSort().isBlank()) {
            String[] parts = filter.getSort().split(";");
            String field = parts[0];

            Sort.Direction direction =
                (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            sorting = Sort.by(direction, field);
        }

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sorting);

        Specification<Booking> spec = BookingSpecification.filter(filter);

        Page<Booking> bookings = bookingRepository.findAll(spec, pageable);

        List<BookingResponse> mapped = bookings.getContent().stream()
            .map(b -> toResponse(b, true, filter.getNeedAttachment()))
            .toList();

        return new PageImpl<>(mapped, pageable, bookings.getTotalElements());
    }

    public Page<BookingResponse> getBookingsNeedApproval(CallerContext callerContext, int page, int size, String sort) {

        Long companyId = callerContext.companyId();

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
        Page<Object[]> idPage = bookingRepository.findBookingIdsNeedApproval(pageable, companyId);

        if (idPage.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Long> bookingIds = idPage.getContent().stream()
            .map(obj -> (Long) obj[0])
            .toList();
        List<Booking> bookings = bookingRepository.findAllById(bookingIds);
        List<BookingFlight> flights = bookingFlightRepository.findByBookingIdIn(bookingIds);
        List<BookingHotel> hotels = bookingHotelRepository.findByBookingIdIn(bookingIds);

        Map<Long, List<BookingFlight>> flightMap =
            flights.stream().collect(Collectors.groupingBy(BookingFlight::getBookingId));

        Map<Long, List<BookingHotel>> hotelMap =
            hotels.stream().collect(Collectors.groupingBy(BookingHotel::getBookingId));

        Map<Long, Booking> bookingMap =
            bookings.stream().collect(Collectors.toMap(Booking::getId, b -> b));

        List<Booking> orderedBookings = bookingIds.stream()
            .map(bookingMap::get)
            .filter(Objects::nonNull)
            .toList();

        List<BookingResponse> responses = orderedBookings.stream().map(b -> toResponse(
                b,
                flightMap.getOrDefault(b.getId(), List.of()),
                hotelMap.getOrDefault(b.getId(), List.of()),
                List.of(),
                false
            ))
            .toList();

        return new PageImpl<>(responses, pageable, idPage.getTotalElements());
    }

    public Page<BookingResponse> getMyApproved(
        CallerContext callerContext,
        int page,
        int size,
        String sort
    ) {

        Long userId = callerContext.userId();
        Long companyId = callerContext.companyId();

        // sorting
        Sort sorting = Sort.by("createdAt").descending();
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(";");
            Sort.Direction direction =
                (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            sorting = Sort.by(direction, parts[0]);
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Long> idPage = bookingApprovalRepository
            .findMyApprovedBookingIds(userId, ApprovalAction.APPROVED, pageable);

        if (idPage.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        List<Long> bookingIds = idPage.getContent();

        List<Booking> bookings = bookingRepository
            .findByIdInAndCompanyId(bookingIds, companyId);

        List<BookingFlight> flights =
            bookingFlightRepository.findByBookingIdIn(bookingIds);

        List<BookingHotel> hotels =
            bookingHotelRepository.findByBookingIdIn(bookingIds);

        Map<Long, List<BookingFlight>> flightMap =
            flights.stream().collect(Collectors.groupingBy(BookingFlight::getBookingId));

        Map<Long, List<BookingHotel>> hotelMap =
            hotels.stream().collect(Collectors.groupingBy(BookingHotel::getBookingId));

        Map<Long, Booking> bookingMap =
            bookings.stream().collect(Collectors.toMap(Booking::getId, b -> b));

        List<BookingResponse> responses = bookingIds.stream()
            .map(bookingMap::get)
            .filter(Objects::nonNull)
            .map(b -> toResponse(
                b,
                flightMap.getOrDefault(b.getId(), List.of()),
                hotelMap.getOrDefault(b.getId(), List.of()),
                List.of(),
                false
            ))
            .toList();

        return new PageImpl<>(responses, pageable, idPage.getTotalElements());
    }

    public BookingResponse getBookingById(CallerContext callerContext, Long id) {
        Long companyId = callerContext.companyId();

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You are not authorized to access this booking");
        }

        return toResponse(booking, true, true);
    }

    public Booking getValidatedBooking(CallerContext callerContext, Long id) {
        Long companyId = callerContext.companyId();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!booking.getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You are not authorized to modify this booking");
        }
        return booking;
    }

    public BookingResponse createBooking(CallerContext callerContext, BookingRequest request){
        Long userId = callerContext.userId();
        Long companyId = callerContext.companyId();

        BookingStatus status = BookingStatus.CREATED;
        Optional<CompanyConfig> isAutoApproveBookingJourney = companyConfigRepository.findByCompanyIdAndCode(companyId, "IS_AUTO_APPROVE_BOOKING_JOURNEY");
        if (isAutoApproveBookingJourney.isPresent()
            && Boolean.TRUE.equals(isAutoApproveBookingJourney.get().getValueBool())) {
            status = BookingStatus.APPROVED;
        }

        Booking booking = Booking.builder()
            .companyId(companyId)
            .code("ANM:"+Instant.now().toEpochMilli())
            .journeyCode(request.getJourneyCode())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .contactEmail(request.getContactEmail())
            .contactFirstName(request.getContactFirstName())
            .contactLastName(request.getContactLastName())
            .contactTitle(request.getContactTitle())
            .contactNationality(request.getContactNationality())
            .contactPhoneCode(request.getContactPhoneCode())
            .contactPhoneNumber(request.getContactPhoneNumber())
            .contactDob(request.getContactDob())
            .additionalInfo(request.getAdditionalInfo())
            .clientAdditionalInfo(request.getClientAdditionalInfo())
            .status(status)
            .createdBy(userId)
            .updatedBy(userId)
            .build();

        bookingRepository.save(booking);

        return toResponse(booking, false, false);
    }

    @Transactional
    public String updateBookingStatus(CallerContext callerContext, BookingUpdateStatusRequest request) {

        String status = request.getStatus();
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }

        int updatedRows = 0;

        switch (request.getType()) {

            case FLIGHT -> {
                BookingFlightStatus statusEnum = BookingFlightStatus.fromString(status);
                if (statusEnum == null) {
                    throw new IllegalArgumentException("Invalid flight status");
                }

                List<BookingFlight> bookingFlights = bookingFlightRepository.findByBookingCode(request.getPartnerBookingId());

                if(!BookingFlightStatus.isValidToUpdate(statusEnum, bookingFlights.get(0).getStatus())) {
                    throw new IllegalArgumentException("Invalid new status");
                }

                updatedRows = bookingFlightRepository.updateStatusByBookingCode(
                    request.getPartnerBookingId(),
                    statusEnum
                );
            }

            case HOTEL -> {
                BookingHotelStatus statusEnum = BookingHotelStatus.fromString(status);
                if (statusEnum == null) {
                    throw new IllegalArgumentException("Invalid hotel status");
                }

                List<BookingHotel> bookingHotels = bookingHotelRepository.findByBookingCode(request.getPartnerBookingId());

                if(!BookingHotelStatus.isValidToUpdate(statusEnum, bookingHotels.get(0).getStatus())) {
                    throw new IllegalArgumentException("Invalid new status");
                }

                updatedRows = bookingHotelRepository.updateStatusByBookingCode(
                    request.getPartnerBookingId(),
                    statusEnum
                );
            }

            default -> {
                throw new IllegalArgumentException("Invalid type");
            }
        }

        if(updatedRows == 0) {
            throw new IllegalArgumentException("No data updated");
        }

        return "Success";
    }

    public BookingResponse toResponse(Booking booking, boolean detail, boolean needAttachment) {
        BookingResponse.BookingResponseBuilder builder = BookingResponse.builder()
                .id(booking.getId())
                .companyId(booking.getCompanyId())
                .code(booking.getCode())
                .journeyCode(booking.getJourneyCode())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .contactEmail(booking.getContactEmail())
                .contactFirstName(booking.getContactFirstName())
                .contactLastName(booking.getContactLastName())
                .contactTitle(booking.getContactTitle())
                .contactNationality(booking.getContactNationality())
                .contactPhoneCode(booking.getContactPhoneCode())
                .contactPhoneNumber(booking.getContactPhoneNumber())
                .contactDob(booking.getContactDob())
                .additionalInfo(booking.getAdditionalInfo())
                .clientAdditionalInfo(booking.getClientAdditionalInfo())
                .status(booking.getStatus());

        if (detail) {
            builder.flights(
                bookingFlightRepository.findByBookingId(booking.getId())
                    .stream()
                    .map(this::toFlightResponse)
                    .toList()
            )
            .hotels(
                bookingHotelRepository.findByBookingId(booking.getId())
                    .stream()
                    .map(this::toHotelResponse)
                    .toList()
            );
        }

        if (needAttachment) {
            builder.attachments(
                bookingAttachmentRepository.findByBookingId(booking.getId())
                    .stream()
                    .map(this::toAttachmentResponse)
                    .toList()
            );
        }

        return builder.build();
    }

    public BookingResponse toResponse(
        Booking booking,
        List<BookingFlight> flights,
        List<BookingHotel> hotels,
        List<BookingAttachment> attachments,
        boolean needAttachment
    ) {
        BookingResponse.BookingResponseBuilder builder = BookingResponse.builder()
            .id(booking.getId())
            .companyId(booking.getCompanyId())
            .code(booking.getCode())
            .journeyCode(booking.getJourneyCode())
            .startDate(booking.getStartDate())
            .endDate(booking.getEndDate())
            .contactEmail(booking.getContactEmail())
            .contactFirstName(booking.getContactFirstName())
            .contactLastName(booking.getContactLastName())
            .contactTitle(booking.getContactTitle())
            .contactNationality(booking.getContactNationality())
            .contactPhoneCode(booking.getContactPhoneCode())
            .contactPhoneNumber(booking.getContactPhoneNumber())
            .contactDob(booking.getContactDob())
            .additionalInfo(booking.getAdditionalInfo())
            .clientAdditionalInfo(booking.getClientAdditionalInfo())
            .status(booking.getStatus());

        builder.flights(
            flights.stream()
                .map(this::toFlightResponse)
                .toList()
        );

        builder.hotels(
            hotels.stream()
                .map(this::toHotelResponse)
                .toList()
        );

        if (needAttachment) {
            builder.attachments(
                attachments.stream()
                    .map(this::toAttachmentResponse)
                    .toList()
            );
        }

        return builder.build();
    }

    private BookingPaxResponse toPaxResponse(BookingPax pax) {
        return BookingPaxResponse.builder()
            .id(pax.getId())
            .bookingId(pax.getBookingId())
            .firstName(pax.getFirstName())
            .lastName(pax.getLastName())
            .title(pax.getTitle())
            .gender(pax.getGender())
            .type(pax.getType())
            .email(pax.getEmail())
            .nationality(pax.getNationality())
            .phoneCode(pax.getPhoneCode())
            .phoneNumber(pax.getPhoneNumber())
            .dob(pax.getDob())
            .addOn(pax.getAddOn())
            .issuingCountry(pax.getIssuingCountry())
            .documentType(pax.getDocumentType())
            .documentNo(pax.getDocumentNo())
            .expirationDate(pax.getExpirationDate())
            .build();
    }

    private BookingFlightResponse toFlightResponse(BookingFlight f) {
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
            .paymentUrl(f.getPaymentUrl())
            .status(f.getStatus())
            .metadata(
                f.getMetadata() != null
                    ? jsonHelper.toJsonNode(f.getMetadata())
                    : jsonHelper.emptyObject()
            )
            .paxs(
                bookingPaxRepository.findByBookingIdAndBookingCode(f.getBookingId(), f.getBookingCode())
                    .stream()
                    .map(this::toPaxResponse)
                    .toList()
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
            .paymentUrl(h.getPaymentUrl())
            .status(h.getStatus())
            .metadata(
                h.getMetadata() != null
                    ? jsonHelper.toJsonNode(h.getMetadata())
                    : jsonHelper.emptyObject()
            )
            .paxs(
                bookingPaxRepository.findByBookingIdAndBookingCode(h.getBookingId(), h.getBookingCode())
                    .stream()
                    .map(this::toPaxResponse)
                    .toList()
            )
            .build();
    }

    private BookingAttachmentResponse toAttachmentResponse(BookingAttachment data) {
        return BookingAttachmentResponse.builder()
            .id(data.getId())
            .companyId(data.getCompanyId())
            .bookingId(data.getBookingId())
            .bookingCode(data.getBookingCode())
            .type(data.getType())
            .file(data.getFile())
            .build();
    }

}
