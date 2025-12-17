package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.queue.BookingPubSubPublisher;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.pubsub.BookingStatusMessage;
import ai.anamaya.service.oms.core.dto.request.BookingApproveRequest;
import ai.anamaya.service.oms.core.dto.request.booking.payment.FlightBookingPaymentRequest;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingFlightSubmitResponse;
import ai.anamaya.service.oms.core.entity.*;
import ai.anamaya.service.oms.core.enums.*;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.*;
import ai.anamaya.service.oms.core.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final BookingPaxRepository bookingPaxRepository;
    private final BookingPubSubPublisher bookingPubSubPublisher;
    private final BookingHotelService bookingHotelService;
    private final JwtUtils jwtUtils;


    private final Map<String, FlightProvider> flightProviders;

    private FlightProvider getFlightProvider(String source) {
        String key = (source != null ? source.toLowerCase() : "biztrip") + "FlightProvider";
        FlightProvider provider = flightProviders.get(key);

        if (provider == null) {
            log.warn("Provider '{}' not found, fallback to 'biztripFlightProvider'", key);
            provider = flightProviders.get("biztripFlightProvider");
        }

        return provider;
    }

    @Transactional(rollbackFor = Exception.class)
    public String approveBooking(CallerContext callerContext, Long bookingId, BookingApproveRequest request) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();

        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You are not authorized to modify this booking");
        }

        if (booking.getStatus() == BookingStatus.CREATED){
            booking.setStatus(BookingStatus.APPROVED);
            booking.setApprovedBy(userId);
            booking.setApprovedByName(userEmail);
            bookingRepository.save(booking);
        }

        List<BookingStatusMessage> bookingStatusMessages = new ArrayList<>();
        if (request.getFlightIds() != null && !request.getFlightIds().isEmpty()) {
            List<BookingFlight> bookingFlights = bookingFlightRepository.findByBookingIdAndIdIn(bookingId, request.getFlightIds());
            List<BookingFlight> notValidFlights = bookingFlights.stream()
                .filter(f -> f.getStatus() != BookingFlightStatus.BOOKED)
                .toList();

            if (!notValidFlights.isEmpty()) {
                throw new IllegalStateException(
                    "Some booking flights are not valid to APPROVED: " +
                        notValidFlights.stream()
                            .map(BookingFlight::getId)
                            .toList()
                );
            }

            bookingFlights.forEach(h -> {
                h.setStatus(BookingFlightStatus.APPROVED);
                h.setUpdatedBy(userId);
            });
            bookingFlightRepository.saveAll(bookingFlights);

            bookingStatusMessages.addAll(
                bookingFlights.stream()
                    .map(f -> BookingStatusMessage.builder()
                        .companyId(booking.getCompanyId())
                        .bookingType(BookingType.FLIGHT)
                        .bookingId(f.getBookingId())
                        .bookingCode(f.getBookingCode())
                        .status(BookingStatus.APPROVED)
                        .build()
                    )
                    .toList()
            );

        }

        if (request.getHotelIds() != null && !request.getHotelIds().isEmpty()) {
            List<BookingHotel> bookingHotels = bookingHotelRepository.findByBookingIdAndIdIn(bookingId, request.getHotelIds());
            List<BookingHotel> notValidHotels = bookingHotels.stream()
                .filter(h -> h.getStatus() != BookingHotelStatus.BOOKED)
                .toList();

            if (!notValidHotels.isEmpty()) {
                throw new IllegalStateException(
                    "Some booking hotels are not valid to APPROVED: " +
                        notValidHotels.stream()
                            .map(BookingHotel::getId)
                            .toList()
                );
            }

            bookingHotels.forEach(h -> {
                h.setStatus(BookingHotelStatus.APPROVED);
                h.setUpdatedBy(userId);
            });
            bookingHotelRepository.saveAll(bookingHotels);

            bookingStatusMessages.addAll(
                bookingHotels.stream()
                    .map(f -> BookingStatusMessage.builder()
                        .companyId(booking.getCompanyId())
                        .bookingType(BookingType.HOTEL)
                        .bookingId(f.getBookingId())
                        .bookingCode(f.getBookingCode())
                        .status(BookingStatus.APPROVED)
                        .build()
                    )
                    .toList()
            );
        }

        for (BookingStatusMessage message : bookingStatusMessages) {
            bookingPubSubPublisher.publishBookingStatus(message);
        }

        return "Booking approved";
    }

    @Transactional
    public void approveConfirmBooking(CallerContext callerContext, BookingStatusMessage request) {
        Booking booking = bookingCommonService.getValidatedBookingById(true, request.getBookingId());

        if(booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalArgumentException("Wrong status");
        }

        switch (request.getBookingType()) {
            case FLIGHT -> {
                ApproveConfirmFlightBooking(callerContext, booking, request);
            }
            case HOTEL -> {
                bookingHotelService.approveProcessBooking(callerContext, booking, request);
            }
        }
    }

    public void ApproveConfirmFlightBooking(CallerContext callerContext, Booking booking, BookingStatusMessage request) {
        Long userId = callerContext.userId();

        List<BookingFlight> bookingFlights = bookingFlightRepository.findByBookingIdAndBookingCode(request.getBookingId(), request.getBookingCode());
        bookingCommonService.bookingDebitBalance(callerContext, booking, bookingFlights, null);
        processFlights(callerContext, booking, bookingFlights);

        bookingFlights.forEach(h -> {
            h.setStatus(BookingFlightStatus.ISSUED);
            h.setUpdatedBy(userId);
        });
        bookingFlightRepository.saveAll(bookingFlights);
    }

    @Transactional
    public void approveConfirmBooking(CallerContext callerContext, Long bookingId) {
        Booking booking = bookingCommonService.getValidatedBookingById(true, bookingId);

        if(booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalArgumentException("Wrong status");
        }

        List<BookingPax> bookingPaxes = bookingPaxRepository.findByBookingId(bookingId);
        List<BookingFlight> bookingFlights = bookingFlightRepository.findByBookingId(bookingId);
        List<BookingHotel> bookingHotels = bookingHotelRepository.findByBookingId(bookingId);

        bookingCommonService.bookingDebitBalance(callerContext, booking, bookingFlights, null);

        if (!bookingFlights.isEmpty()) {
            processFlights(callerContext, booking, bookingFlights);
        }

        if (!bookingHotels.isEmpty()) {
//            processHotels(callerContext, booking, bookingPaxes, bookingHotels);
        }

        booking.setStatus(BookingStatus.ISSUED);
    }

    private void processFlights(
        CallerContext callerContext,
        Booking booking,
        List<BookingFlight> bookingFlights
    ) {
        FlightProvider provider = getFlightProvider("biztrip");

        BookingFlightSubmitResponse response = provider.payment(
            callerContext,
            FlightBookingPaymentRequest.builder()
                .bookingId(bookingFlights.get(0).getBookingReference())
                .paymentMethod("DEPOSIT")
                .build()
        );

        BookingFlightStatus flightStatus =
            BookingFlightStatus.fromPaymentPartnerStatus(
                response.getBookingSubmissionStatus()
            );

        bookingFlightHistoryRepository.save(
            BookingFlightHistory.builder()
                .bookingId(booking.getId())
                .status(flightStatus)
                .data(response)
                .build()
        );

        if (flightStatus != BookingFlightStatus.CREATED) {
            throw new IllegalStateException(
                "Flight payment failed: " + flightStatus
            );
        }
    }


}
