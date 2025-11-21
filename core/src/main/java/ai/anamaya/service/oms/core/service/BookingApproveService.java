package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.queue.BookingPubSubPublisher;
import ai.anamaya.service.oms.core.dto.pubsub.BookingStatusMessage;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.enums.BookingStatus;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.BookingRepository;
import ai.anamaya.service.oms.core.security.JwtUtils;
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
    private final BookingPubSubPublisher bookingPubSubPublisher;
    private final JwtUtils jwtUtils;

    public String approveBooking(Long id) {
        Long companyId = jwtUtils.getCompanyIdFromToken();

        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You are not authorized to modify this booking");
        }

        if (booking.getStatus() != BookingStatus.CREATED){
            throw new IllegalArgumentException("Wrong status");
        }

        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        BookingStatusMessage message =
            new BookingStatusMessage(booking.getId(), booking.getStatus());

        bookingPubSubPublisher.publishBookingStatus(message);


        return "Booking approved";
    }

    public String approveConfirmBooking(Long id) {
        return "Booking approved confirm";
    }

}
