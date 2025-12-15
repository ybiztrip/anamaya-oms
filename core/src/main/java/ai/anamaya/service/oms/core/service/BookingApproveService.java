package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.queue.BookingPubSubPublisher;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.pubsub.BookingStatusMessage;
import ai.anamaya.service.oms.core.dto.request.BookingApproveRequest;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingCheckRateRequest;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingCreateRequest;
import ai.anamaya.service.oms.core.dto.request.booking.payment.FlightBookingPaymentRequest;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCheckRateResponse;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCreateResponse;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingFlightSubmitResponse;
import ai.anamaya.service.oms.core.entity.*;
import ai.anamaya.service.oms.core.enums.*;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.*;
import ai.anamaya.service.oms.core.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


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
    private final BalanceService balanceService;
    private final JwtUtils jwtUtils;


    private final Map<String, FlightProvider> flightProviders;
    private final Map<String, HotelProvider> hotelProviders;

    private FlightProvider getFlightProvider(String source) {
        String key = (source != null ? source.toLowerCase() : "biztrip") + "FlightProvider";
        FlightProvider provider = flightProviders.get(key);

        if (provider == null) {
            log.warn("Provider '{}' not found, fallback to 'biztripFlightProvider'", key);
            provider = flightProviders.get("biztripFlightProvider");
        }

        return provider;
    }

    private HotelProvider getHotelProvider(String source) {
        String key = (source != null ? source.toLowerCase() : "biztrip") + "HotelProvider";
        HotelProvider provider = hotelProviders.get(key);

        if (provider == null) {
            log.warn("Provider '{}' not found, fallback to 'biztripHotelProvider'", key);
            provider = hotelProviders.get("biztripHotelProvider");
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

        if (bookingStatusMessages.isEmpty()) {
            throw new IllegalArgumentException("No booking has been approved");
        }
        for (BookingStatusMessage message : bookingStatusMessages) {
            bookingPubSubPublisher.publishBookingStatus(message);
        }

        return "Booking approved";
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

        bookingCommonService.bookingDebitBalance(callerContext, booking, bookingFlights, bookingHotels);

        if (!bookingFlights.isEmpty()) {
            processFlights(callerContext, booking, bookingFlights);
        }

        if (!bookingHotels.isEmpty()) {
            processHotels(callerContext, booking, bookingPaxes, bookingHotels);
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

    private void processHotels(
        CallerContext callerContext,
        Booking booking,
        List<BookingPax> bookingPaxes,
        List<BookingHotel> bookingHotels
    ) {
        HotelProvider provider = getHotelProvider("biztrip");

        for (BookingHotel hotel : bookingHotels) {

            HotelBookingCheckRateResponse rateResponse =
                provider.checkRate(
                    callerContext,
                    buildHotelBookingCheckRateRequest(hotel, bookingPaxes)
                );

            if (!"AVAILABLE".equals(rateResponse.getRateStatus())) {
                throw new IllegalStateException(
                    "Hotel rate not available for bookingId=" + booking.getId()
                );
            }

            hotel.setPaymentKey(rateResponse.getPaymentKey());
            HotelBookingCreateResponse createResponse =
                provider.create(
                    callerContext,
                    buildHotelBookingCreateRequest(booking, bookingPaxes, hotel)
                );

            BookingHotelStatus hotelStatus =
                BookingHotelStatus.fromBookingPartnerStatus(
                    createResponse.getStatus()
                );

            hotel.setStatus(hotelStatus);
            hotel.setBookingReference(createResponse.getBookingReference());
            hotel.setPartnerSellAmount(
                Double.valueOf(createResponse.getTotalAmount())
            );
            hotel.setCurrency(createResponse.getCurrency());
        }
    }

    private HotelBookingCheckRateRequest buildHotelBookingCheckRateRequest(
        BookingHotel hotel,
        List<BookingPax> bookingPaxes
    ) {

        long numAdults =
            bookingPaxes.stream()
                .filter(p -> p.getType() == PaxType.ADULT)
                .count();

        return HotelBookingCheckRateRequest.builder()
                .propertyId(hotel.getItemId())
                .roomId(hotel.getRoomId())
                .checkInDate(hotel.getCheckInDate().toString())
                .checkOutDate(hotel.getCheckOutDate().toString())
                .numRooms(hotel.getNumRoom().intValue())
                .numAdults((int) numAdults)
                .displayCurrency(hotel.getCurrency())
                .userNationality("ID")
                .rateKey(hotel.getRateKey())
                .build();
    }

    private HotelBookingCreateRequest buildHotelBookingCreateRequest(
        Booking booking,
        List<BookingPax> bookingPaxes,
        BookingHotel hotel
    ) {

        List<HotelBookingCreateRequest.GuestInfo> guests =
            bookingPaxes.stream()
                .filter(pax -> pax.getType() == PaxType.ADULT)
                .map(pax ->
                    HotelBookingCreateRequest.GuestInfo.builder()
                        .title(pax.getTitle() != null
                            ? pax.getTitle().name()
                            : null)
                        .firstName(pax.getFirstName())
                        .lastName(pax.getLastName())
                        .email(pax.getEmail())
                        .gender(pax.getGender() != null
                            ? pax.getGender().name()
                            : null)
                        .idtype(pax.getDocumentType())
                        .idnumber(pax.getDocumentNo())
                        .build()
                )
                .toList();

        long numAdults =
            bookingPaxes.stream()
                .filter(p -> p.getType() == PaxType.ADULT)
                .count();

        List<Integer> childrenAges =
            bookingPaxes.stream()
                .filter(p -> p.getType() == PaxType.CHILD)
                .map(p -> p.getDob() != null
                    ? LocalDate.now().getYear() - p.getDob().getYear()
                    : null)
                .filter(Objects::nonNull)
                .toList();

        return HotelBookingCreateRequest.builder()
            .propertyId(hotel.getItemId())
            .partnerBookingId(booking.getCode())
            .checkInDate(hotel.getCheckInDate().toString())
            .checkOutDate(hotel.getCheckOutDate().toString())
            .displayCurrency(hotel.getCurrency())
            .specialRequest(hotel.getSpecialRequest())
            .language("en")
            .userNationality("ID")

            .customerInfo(
                HotelBookingCreateRequest.CustomerInfo.builder()
                    .title(booking.getContactTitle() != null
                        ? booking.getContactFirstName()+ " " + booking.getContactLastName()
                        : null)
                    .firstName(booking.getContactFirstName())
                    .lastName(booking.getContactLastName())
                    .email(booking.getContactEmail())
                    .phone(booking.getContactPhoneNumber())
                    .build()
            )

            .rooms(List.of(
                HotelBookingCreateRequest.Room.builder()
                    .roomId(hotel.getRoomId())
                    .rateKey(hotel.getRateKey())
                    .paymentKey(hotel.getPaymentKey())
                    .numRooms(hotel.getNumRoom().intValue())
                    .numAdults((int) numAdults)
                    .numChild(childrenAges.size())
                    .childrenAges(childrenAges)
                    .guestInfo(guests)
                    .build()
            ))

            .totalRates(
                HotelBookingCreateRequest.TotalRates.builder()
                    .partnerSellAmount(
                        String.valueOf(hotel.getPartnerSellAmount())
                    )
                    .partnerNettAmount(
                        String.valueOf(hotel.getPartnerNettAmount())
                    )
                    .build()
            )

            .userPayment(
                HotelBookingCreateRequest.UserPayment.builder()
                    .userPayment("DEPOSIT")
                    .build()
            )

            .additionalData(
                booking.getAdditionalInfo() != null
                    ? booking.getAdditionalInfo().toString()
                    : "{}"
            )
            .build();
    }




    public String retryApproveConfirmBooking(Long id) {
        return "Booking approved confirm";
    }

}
