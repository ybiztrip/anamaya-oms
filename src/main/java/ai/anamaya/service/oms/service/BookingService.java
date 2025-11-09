package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.BookingRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.BookingResponse;
import ai.anamaya.service.oms.entity.Booking;
import ai.anamaya.service.oms.enums.BookingStatus;
import ai.anamaya.service.oms.repository.BookingRepository;
import ai.anamaya.service.oms.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final JwtUtils jwtUtils;

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
            .status(BookingStatus.CREATED)
            .createdBy(userId)
            .updatedBy(userId)
            .build();

        bookingRepository.save(booking);

        return ApiResponse.success(toResponse(booking));
    }

    private BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
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
                .status(booking.getStatus())
                .passengers(Collections.emptyList())
                .build();
    }
}
