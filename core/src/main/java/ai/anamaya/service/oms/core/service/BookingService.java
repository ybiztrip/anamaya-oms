package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.dto.request.BookingListFilter;
import ai.anamaya.service.oms.core.dto.request.BookingRequest;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.entity.*;
import ai.anamaya.service.oms.core.enums.BookingStatus;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.BookingFlightRepository;
import ai.anamaya.service.oms.core.repository.BookingHotelRepository;
import ai.anamaya.service.oms.core.repository.BookingPaxRepository;
import ai.anamaya.service.oms.core.repository.BookingRepository;
import ai.anamaya.service.oms.core.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingPaxRepository bookingPaxRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final BookingHotelRepository bookingHotelRepository;
    private final JwtUtils jwtUtils;


    public Page<BookingResponse> getAll(int page, int size, String sort, BookingListFilter filter) {

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

        Specification<Booking> spec = BookingSpecification.filter(filter);

        Page<Booking> bookings = bookingRepository.findAll(spec, pageable);

        List<BookingResponse> mapped = bookings.getContent().stream()
            .map(b -> toResponse(b, false, false))
            .toList();

        return new PageImpl<>(mapped, pageable, bookings.getTotalElements());
    }

    public static class BookingSpecification {

        public static Specification<Booking> filter(BookingListFilter filter) {
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

    public BookingResponse getBookingById(Long id) {
        Long companyId = jwtUtils.getCompanyIdFromToken();

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You are not authorized to access this booking");
        }

        return toResponse(booking, true, true);
    }

    public Booking getValidatedBooking(Long id) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!booking.getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You are not authorized to modify this booking");
        }
        return booking;
    }

    public BookingResponse createBooking(BookingRequest request){
        Long userId = jwtUtils.getUserIdFromToken();
        Long companyId = jwtUtils.getCompanyIdFromToken();
        ObjectMapper mapper = new ObjectMapper();

        Booking booking = Booking.builder()
            .companyId(companyId)
            .code(String.valueOf(Instant.now().toEpochMilli()))
            .journeyCode(request.getJourneyCode())
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
            .status(BookingStatus.DRAFT)
            .createdBy(userId)
            .updatedBy(userId)
            .build();

        bookingRepository.save(booking);

        return toResponse(booking, false, false);
    }

    public BookingResponse toResponse(Booking booking, boolean pax, boolean detail) {
        BookingResponse.BookingResponseBuilder builder = BookingResponse.builder()
                .id(booking.getId())
                .companyId(booking.getCompanyId())
                .code(booking.getCode())
                .journeyCode(booking.getJourneyCode())
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

        if (pax) {
            builder.paxList(
                bookingPaxRepository.findByBookingId(booking.getId())
                .stream()
                .map(this::toPaxResponse)
                .toList()
            );
        }

        if (detail) {
            builder.flightList(
                bookingFlightRepository.findByBookingId(booking.getId())
                    .stream()
                    .map(this::toFlightResponse)
                    .toList()
            )
            .hotelList(
                bookingHotelRepository.findByBookingId(booking.getId())
                    .stream()
                    .map(this::toHotelResponse)
                    .toList()
            );
        }

        return builder.build();
    }

    private BookingPaxResponse toPaxResponse(BookingPax pax) {
        return BookingPaxResponse.builder()
                .id(pax.getId())
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
                .bookingId(f.getBookingId())
                .type(f.getType())
                .clientSource(f.getClientSource())
                .itemId(f.getItemId())
                .origin(f.getOrigin())
                .destination(f.getDestination())
                .departureDatetime(f.getDepartureDatetime())
                .arrivalDatetime(f.getArrivalDatetime())
                .status(f.getStatus())
                .build();
    }

    private BookingHotelResponse toHotelResponse(BookingHotel h) {
        return BookingHotelResponse.builder()
                .id(h.getId())
                .bookingId(h.getBookingId())
                .clientSource(h.getClientSource())
                .itemId(h.getItemId())
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
