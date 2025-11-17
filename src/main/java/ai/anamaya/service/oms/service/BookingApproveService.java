package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.pubsub.BookingStatusMessage;
import ai.anamaya.service.oms.dto.response.*;
import ai.anamaya.service.oms.entity.Booking;
import ai.anamaya.service.oms.enums.BookingStatus;
import ai.anamaya.service.oms.exception.AccessDeniedException;
import ai.anamaya.service.oms.exception.NotFoundException;
import ai.anamaya.service.oms.repository.BookingRepository;
import ai.anamaya.service.oms.security.JwtUtils;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class BookingApproveService {

    private final BookingRepository bookingRepository;
    private final JwtUtils jwtUtils;

    public ApiResponse<String> approveBooking(Long id) {
        Long companyId = jwtUtils.getCompanyIdFromToken();

        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You are not authorized to modify this booking");
        }

//        if (booking.getStatus() != BookingStatus.CREATED){
//            throw new IllegalArgumentException("Wrong status");
//        }
//
//        booking.setStatus(BookingStatus.APPROVED);
//        bookingRepository.save(booking);

//        BookingStatusMessage message =
//            new BookingStatusMessage(booking.getId(), booking.getStatus());
//
//        bookingPubSubPublisher.publishBookingStatus(message);


        return ApiResponse.success("Booking approved");
    }

}
