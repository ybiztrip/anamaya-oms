package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.BookingRequest;
import ai.anamaya.service.oms.dto.response.*;
import ai.anamaya.service.oms.entity.Booking;
import ai.anamaya.service.oms.entity.BookingFlight;
import ai.anamaya.service.oms.entity.BookingHotel;
import ai.anamaya.service.oms.entity.BookingPax;
import ai.anamaya.service.oms.enums.BookingStatus;
import ai.anamaya.service.oms.exception.AccessDeniedException;
import ai.anamaya.service.oms.exception.NotFoundException;
import ai.anamaya.service.oms.repository.BookingFlightRepository;
import ai.anamaya.service.oms.repository.BookingHotelRepository;
import ai.anamaya.service.oms.repository.BookingPaxRepository;
import ai.anamaya.service.oms.repository.BookingRepository;
import ai.anamaya.service.oms.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingPaxRepository bookingPaxRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final BookingHotelRepository bookingHotelRepository;
    private final JwtUtils jwtUtils;

    public ApiResponse<BookingResponse> getBookingById(Long id) {
        Long companyId = jwtUtils.getCompanyIdFromToken();

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You are not authorized to access this booking");
        }

        return ApiResponse.success(toResponse(booking, true, true));
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

    public ApiResponse<BookingResponse> createBooking(BookingRequest request){
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

        return ApiResponse.success(toResponse(booking, false, false));
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
