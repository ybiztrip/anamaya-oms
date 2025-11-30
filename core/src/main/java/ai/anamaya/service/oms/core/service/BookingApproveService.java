package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.queue.BookingPubSubPublisher;
import ai.anamaya.service.oms.core.dto.pubsub.BookingStatusMessage;
import ai.anamaya.service.oms.core.dto.request.BalanceAdjustRequest;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.enums.*;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.BookingFlightRepository;
import ai.anamaya.service.oms.core.repository.BookingHotelRepository;
import ai.anamaya.service.oms.core.repository.BookingRepository;
import ai.anamaya.service.oms.core.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class BookingApproveService {

    private final BookingCommonService bookingCommonService;
    private final BookingRepository bookingRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final BookingHotelRepository bookingHotelRepository;
    private final BookingPubSubPublisher bookingPubSubPublisher;
    private final BalanceService balanceService;
    private final JwtUtils jwtUtils;

    public String approveBooking(Long id) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();

        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You are not authorized to modify this booking");
        }

        if (booking.getStatus() != BookingStatus.CREATED){
            throw new IllegalArgumentException("Wrong status");
        }

        booking.setStatus(BookingStatus.APPROVED);
        booking.setApprovedBy(userId);
        booking.setApprovedByName(userEmail);
        bookingRepository.save(booking);

        BookingStatusMessage message =
            new BookingStatusMessage(booking.getId(), booking.getStatus());

        bookingPubSubPublisher.publishBookingStatus(message);

        return "Booking approved";
    }

    @Transactional
    public void approveConfirmBooking(Long id) {
        Booking booking = bookingCommonService.getValidatedBookingById(true, id);

        if(booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalArgumentException("Wrong status");
        }

        List<BookingFlight> bookingFlights = bookingFlightRepository.findByBookingId(id);
        List<BookingHotel> bookingHotels = bookingHotelRepository.findByBookingId(id);

        bookingCommonService.bookingDebitBalance(booking, bookingFlights, bookingHotels);
    }


    public String retryApproveConfirmBooking(Long id) {
        return "Booking approved confirm";
    }

}
