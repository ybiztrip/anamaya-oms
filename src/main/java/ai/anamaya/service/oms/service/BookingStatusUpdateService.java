package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.pubsub.BookingStatusMessage;
import ai.anamaya.service.oms.dto.request.BalanceAdjustRequest;
import ai.anamaya.service.oms.entity.Booking;
import ai.anamaya.service.oms.enums.BookingStatus;
import ai.anamaya.service.oms.exception.NotFoundException;
import ai.anamaya.service.oms.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingStatusUpdateService {

    private final BookingRepository bookingRepository;
    private final BalanceService balanceService;

    @Transactional
    public void handleBookingStatusUpdate(BookingStatusMessage message) {
        Booking booking = bookingRepository.findById(message.getBookingId())
            .orElseThrow(() -> new NotFoundException("Booking not found"));

        BookingStatus newStatus = message.getStatus();

        switch (newStatus) {
            case APPROVED -> updateStatusApproved(booking, message);
            case REJECTED -> updateStatusRejected(booking);
            case CANCELLED -> updateStatusCancelled(booking);
            case ISSUED -> updateStatusIssued(booking);
            default -> log.warn("Status {} not handled", newStatus);
        }
    }

    private void updateStatusApproved(Booking booking, BookingStatusMessage message) {
        log.info("Updating booking {} to APPROVED", booking.getId());

//        balanceService.adjustBalance(BalanceAdjustRequest());

        booking.setStatus(BookingStatus.ON_PROCESS);
        bookingRepository.save(booking);
    }

    private void updateStatusRejected(Booking booking) {
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
    }

    private void updateStatusCancelled(Booking booking) {
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private void updateStatusIssued(Booking booking) {
        booking.setStatus(BookingStatus.ISSUED);
        bookingRepository.save(booking);
    }
}
