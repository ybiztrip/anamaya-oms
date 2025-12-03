package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.queue.BookingPubSubPublisher;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.pubsub.BookingStatusMessage;
import ai.anamaya.service.oms.core.dto.request.booking.payment.BookingPaymentRequest;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingSubmitResponse;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.entity.BookingFlightHistory;
import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.enums.*;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.BookingFlightHistoryRepository;
import ai.anamaya.service.oms.core.repository.BookingFlightRepository;
import ai.anamaya.service.oms.core.repository.BookingHotelRepository;
import ai.anamaya.service.oms.core.repository.BookingRepository;
import ai.anamaya.service.oms.core.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingApproveService {

    private final BookingCommonService bookingCommonService;
    private final BookingRepository bookingRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final BookingFlightHistoryRepository bookingFlightHistoryRepository;
    private final BookingHotelRepository bookingHotelRepository;
    private final BookingPubSubPublisher bookingPubSubPublisher;
    private final BalanceService balanceService;
    private final JwtUtils jwtUtils;


    private final Map<String, FlightProvider> flightProviders;
    private final ObjectMapper mapper = new ObjectMapper();

    private FlightProvider getProvider(String source) {
        String key = (source != null ? source.toLowerCase() : "biztrip") + "FlightProvider";
        FlightProvider provider = flightProviders.get(key);

        if (provider == null) {
            log.warn("Provider '{}' not found, fallback to 'biztripFlightProvider'", key);
            provider = flightProviders.get("biztripFlightProvider");
        }

        return provider;
    }

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
            new BookingStatusMessage(booking.getId(), booking.getCompanyId(), booking.getStatus());

        bookingPubSubPublisher.publishBookingStatus(message);

        return "Booking approved";
    }

    @Transactional
    public void approveConfirmBooking(CallerContext callerContext, Long bookingId) {
        Booking booking = bookingCommonService.getValidatedBookingById(true, bookingId);

        if(booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalArgumentException("Wrong status");
        }

        List<BookingFlight> bookingFlights = bookingFlightRepository.findByBookingId(bookingId);
        List<BookingHotel> bookingHotels = bookingHotelRepository.findByBookingId(bookingId);

        bookingCommonService.bookingDebitBalance(booking, bookingFlights, bookingHotels);

        FlightProvider provider = getProvider("biztrip");
        BookingSubmitResponse response = provider.payment(
            callerContext,
            BookingPaymentRequest.builder()
                .bookingId(bookingFlights.get(0).getBookingReference())
                .paymentMethod("DEPOSIT")
                .build()
        );

        BookingFlightStatus bookingFlightStatus = BookingFlightStatus.fromPaymentPartnerStatus(response.getBookingSubmissionStatus());
        if(bookingFlightStatus == BookingFlightStatus.CREATED){
            return;
        }

        bookingFlightHistoryRepository.save(
            BookingFlightHistory.builder()
                .bookingId(bookingId)
                .status(bookingFlightStatus)
                .data(response)
                .build()
        );

        booking.setStatus(BookingStatus.ISSUED);
    }


    public String retryApproveConfirmBooking(Long id) {
        return "Booking approved confirm";
    }

}
