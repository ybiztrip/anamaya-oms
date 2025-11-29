package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.dto.request.booking.status.BookingStatusCheckRequest;
import ai.anamaya.service.oms.core.dto.request.booking.submit.*;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingSubmitResponse;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.entity.BookingFlightHistory;
import ai.anamaya.service.oms.core.entity.BookingPax;
import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import ai.anamaya.service.oms.core.enums.BookingStatus;
import ai.anamaya.service.oms.core.enums.PaxType;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.BookingFlightHistoryRepository;
import ai.anamaya.service.oms.core.repository.BookingFlightRepository;
import ai.anamaya.service.oms.core.repository.BookingPaxRepository;
import ai.anamaya.service.oms.core.repository.BookingRepository;
import ai.anamaya.service.oms.core.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingSubmitService {

    private final BookingRepository bookingRepository;
    private final BookingPaxRepository bookingPaxRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final BookingFlightHistoryRepository bookingFlightHistoryRepository;
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

    @Transactional
    public BookingSubmitResponse submitBooking(Long bookingId) {

        Booking booking = getValidatedBooking(bookingId);

        if(booking.getStatus() != BookingStatus.DRAFT) {
            throw new IllegalArgumentException("Wrong status");
        }

        booking.setStatus(BookingStatus.CREATED);

        List<BookingPax> pax = bookingPaxRepository.findByBookingId(bookingId);
        List<BookingFlight> flights = bookingFlightRepository.findByBookingId(bookingId);
        if(flights == null || flights.isEmpty()) {
            return null;
        }

        BookingSubmitRequest request = buildSubmitRequest(booking, pax, flights);

        FlightProvider provider = getProvider("biztrip");
        BookingSubmitResponse response = provider.submitBooking(request);

        updateBookingFlightAmounts(bookingId, response);
        BookingFlightStatus bookingFlightStatus = BookingFlightStatus.fromPartnerStatus(response.getBookingSubmissionStatus());
        bookingFlightHistoryRepository.save(
            BookingFlightHistory.builder()
                .bookingId(bookingId)
                .status(bookingFlightStatus)
                .data(response.toString())
                .build()
        );

        if(bookingFlightStatus == BookingFlightStatus.CREATED) {
            booking.setStatus(BookingStatus.ON_PROCESS_CREATE);
        }

        return response;
    }

    @Transactional
    public void retryBookingSubmit(Long bookingId) {

        Booking booking = getValidatedBooking(bookingId);

        if(booking.getStatus() != BookingStatus.ON_PROCESS_CREATE) {
            throw new IllegalArgumentException("Wrong status");
        }

        List<BookingFlight> processingFlights = bookingFlightRepository.findByBookingId(bookingId);
        if (processingFlights == null || processingFlights.isEmpty()) {
            return;
        }

        List<String> bookingReferenceIds = processingFlights.stream()
            .filter(f -> f.getStatus() == BookingFlightStatus.CREATED)
            .map(BookingFlight::getBookingReference)
            .toList();

        FlightProvider provider = getProvider("biztrip");
        BookingSubmitResponse response = provider.checkStatus(BookingStatusCheckRequest.builder()
            .bookingReferenceIds(bookingReferenceIds)
            .build()
        );

        if(!BookingFlightStatus.isSuccessBook(response.getBookingSubmissionStatus())) {
            return;
        }

        //update booking_flight price
        BookingFlightStatus bookingFlightStatus = BookingFlightStatus.fromPartnerStatus(response.getBookingSubmissionStatus());
        bookingFlightRepository.updateStatusByBookingReferences(bookingId,
            bookingReferenceIds,
            bookingFlightStatus
        );
        bookingFlightHistoryRepository.save(
            BookingFlightHistory.builder()
                .bookingId(bookingId)
                .status(bookingFlightStatus)
                .data(response.toString())
                .build()
        );
        booking.setStatus(BookingStatus.CREATED);
    }

    private Booking getValidatedBooking(Long id) {
        Long companyId = jwtUtils.getCompanyIdFromToken();

        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You are not authorized to access this booking");
        }

        return booking;
    }

    private BookingSubmitRequest buildSubmitRequest(
        Booking booking,
        List<BookingPax> paxList,
        List<BookingFlight> flightList
    ) {

        ContactDetail contact = ContactDetail.builder()
            .email(booking.getContactEmail())
            .firstName(booking.getContactFirstName())
            .lastName(booking.getContactLastName())
            .phoneNumber(booking.getContactPhoneNumber())
            .phoneNumberCountryCode(booking.getContactPhoneCode())
            .customerEmail(booking.getContactEmail())
            .customerPhoneNumber(booking.getContactPhoneNumber())
            .customerPhoneNumberCountryCode(booking.getContactPhoneCode())
            .title(booking.getContactTitle())
            .dateOfBirth(
                booking.getContactDob() != null
                    ? booking.getContactDob().toString()
                    : null
            )
            .build();

        List<Passenger> adults = paxList.stream()
            .filter(p -> p.getType() == PaxType.ADULT)
            .map(pax -> Passenger.builder()
                .title(pax.getTitle() != null ? pax.getTitle().name() : null)
                .firstName(pax.getFirstName())
                .lastName(pax.getLastName())
                .gender(pax.getGender() != null ? pax.getGender().name() : null)
                .dateOfBirth(pax.getDob() != null ? pax.getDob().toString() : null)
                .nationality(pax.getNationality())
                .documentDetail(new DocumentDetail(
                    pax.getIssuingCountry(),
                    pax.getDocumentNo(),
                    pax.getExpirationDate() != null ? pax.getExpirationDate().toString() : null,
                    pax.getDocumentType()
                ))
                .addOns(pax.getAddOn() != null ? mapper.convertValue(pax.getAddOn(), List.class) : null)
                .build()
            ).toList();


        return BookingSubmitRequest.builder()
            .contactDetail(contact)
            .passengers(new Passengers(adults, null, null))
            .flightIds(flightList.stream().map(BookingFlight::getItemId).toList())
            .destinationId(booking.getJourneyCode())
            .journeyType(flightList.size() > 1 ? "ROUND_TRIP" : "ONE_WAY")
            .locale("id_ID")
            .loginID(booking.getContactEmail())
            .loginType("EMAIL")
            .customerLoginID(booking.getContactEmail())
            .customerLoginType("EMAIL")
            .source("GSP")
            .jabatan("")
            .additionalData(booking.getAdditionalInfo() != null
                ? booking.getAdditionalInfo().toString()
                : "{}")
            .build();
    }

    private void updateBookingFlightAmounts(Long bookingId, BookingSubmitResponse response) {

        var detail = response.getFlightBookingDetail();
        var fare = detail.getFareDetail();

        List<BookingFlight> flights = bookingFlightRepository.findByBookingId(bookingId);

        for (BookingFlight f : flights) {
            if (fare.getAdultFare() != null)
                f.setAdultAmount(BigDecimal.valueOf(fare.getAdultFare().getAmount()));

            if (fare.getChildFare() != null)
                f.setChildAmount(BigDecimal.valueOf(fare.getChildFare().getAmount()));

            if (fare.getInfantFare() != null)
                f.setInfantAmount(BigDecimal.valueOf(fare.getInfantFare().getAmount()));

            f.setTotalAmount(BigDecimal.valueOf(detail.getGrandTotalFareWithCurrency().getAmount()));

            bookingFlightRepository.save(f);
        }
    }
}
