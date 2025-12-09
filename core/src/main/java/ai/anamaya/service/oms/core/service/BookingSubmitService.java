package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.booking.submit.*;
import ai.anamaya.service.oms.core.dto.response.booking.data.BookingDataResponse;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingFlightSubmitResponse;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.entity.BookingFlightHistory;
import ai.anamaya.service.oms.core.entity.BookingPax;
import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import ai.anamaya.service.oms.core.enums.BookingStatus;
import ai.anamaya.service.oms.core.enums.PaxType;
import ai.anamaya.service.oms.core.repository.BookingFlightHistoryRepository;
import ai.anamaya.service.oms.core.repository.BookingFlightRepository;
import ai.anamaya.service.oms.core.repository.BookingPaxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingSubmitService {

    private final BookingCommonService bookingCommonService;
    private final BookingPaxRepository bookingPaxRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final BookingFlightHistoryRepository bookingFlightHistoryRepository;

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
    public BookingFlightSubmitResponse submitBooking(CallerContext callerContext, Long bookingId) {
        Booking booking = bookingCommonService.getValidatedBookingById(false, bookingId);

        if(booking.getStatus() != BookingStatus.DRAFT) {
            throw new IllegalArgumentException("Wrong status");
        }

        booking.setStatus(BookingStatus.CREATED);

        List<BookingPax> pax = bookingPaxRepository.findByBookingId(bookingId);
        List<BookingFlight> flights = bookingFlightRepository.findByBookingId(bookingId);
        if(flights == null || flights.isEmpty()) {
            return null;
        }

        FlightBookingSubmitRequest request = buildSubmitRequest(booking, pax, flights);

        FlightProvider provider = getProvider("biztrip");
        BookingFlightSubmitResponse response = provider.submitBooking(request);
        BookingFlightStatus bookingFlightStatus = BookingFlightStatus.fromBookingPartnerStatus(response.getBookingSubmissionStatus());
        bookingFlightHistoryRepository.save(
            BookingFlightHistory.builder()
                .bookingId(bookingId)
                .status(bookingFlightStatus)
                .data(response)
                .build()
        );

        if(bookingFlightStatus == BookingFlightStatus.CREATED) {
            booking.setStatus(BookingStatus.ON_PROCESS_CREATE);
        }

        if(response.getPaymentExpirationTime() != null && response.getPaymentExpirationTime() > 0) {
            Long epochMillis = response.getPaymentExpirationTime();
            booking.setPaymentExpirationTime(Instant.ofEpochMilli(epochMillis).atOffset(ZoneOffset.UTC));
        }

        List<String> bookingReferenceCodes = flights.stream()
            .filter(f -> f.getStatus() == BookingFlightStatus.CREATED)
            .map(BookingFlight::getBookingReference)
            .toList();

        List<BookingDataResponse> bookingDataResponse = provider.searchData(callerContext, FlightBookingSearchDataRequest.builder()
            .count(100)
            .page(0)
            .referenceCodes(bookingReferenceCodes)
            .build());

        updateBookingFlightData(bookingId, bookingDataResponse);

        return response;
    }

    @Transactional
    public void retryBookingSubmit(CallerContext callerContext, Long bookingId) {

        Booking booking = bookingCommonService.getValidatedBookingById(true, bookingId);

        if(booking.getStatus() != BookingStatus.ON_PROCESS_CREATE) {
            log.debug("Skip retryBookingSubmit for bookingId={}, status={}", bookingId, booking.getStatus());
            return;
        }

        List<BookingFlight> processingFlights = bookingFlightRepository.findByBookingId(bookingId);
        if (processingFlights == null || processingFlights.isEmpty()) {
            return;
        }

        List<String> bookingReferenceCodes = processingFlights.stream()
            .filter(f -> f.getStatus() == BookingFlightStatus.CREATED)
            .map(BookingFlight::getBookingReference)
            .toList();

        FlightProvider provider = getProvider("biztrip");
        List<BookingDataResponse> bookingDataResponse = provider.searchData(callerContext, FlightBookingSearchDataRequest.builder()
            .count(100)
            .page(0)
            .referenceCodes(bookingReferenceCodes)
            .build());

        updateBookingFlightData(bookingId, bookingDataResponse);

        boolean allBooked = bookingDataResponse.stream()
            .allMatch(b ->
                BookingFlightStatus.fromBookingPartnerStatus(b.getStatus()).equals(BookingFlightStatus.BOOKED)
            );

        if (allBooked) {
            booking.setStatus(BookingStatus.CREATED);
        }

    }

    private FlightBookingSubmitRequest buildSubmitRequest(
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


        return FlightBookingSubmitRequest.builder()
            .contactDetail(contact)
            .passengers(new Passengers(adults, null, null))
            .flightIds(flightList.stream().map(BookingFlight::getItemId).toList())
            .partnerBookingId(booking.getCode())
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

    private void updateBookingFlightData(Long bookingId, List<BookingDataResponse> responses) {
        List<BookingFlight> flights = bookingFlightRepository.findByBookingId(bookingId);

        for (BookingFlight f : flights) {
            if (f.getStatus().equals(BookingFlightStatus.BOOKED)) {
                continue;
            }

            for (BookingDataResponse data : responses) {
                if (!data.getDeparture().equals(f.getOrigin()) && !data.getArrival().equals(f.getDestination()) ) {
                    continue;
                }

                if (data.getAdultPrice() != null
                    && data.getAdultPrice().compareTo(BigDecimal.ZERO) > 0) {
                    f.setAdultAmount(data.getAdultPrice());
                }

                if (data.getChildPrice() != null
                    && data.getChildPrice().compareTo(BigDecimal.ZERO) > 0) {
                    f.setChildAmount(data.getChildPrice());
                }

                if (data.getInfantPrice() != null
                    && data.getInfantPrice().compareTo(BigDecimal.ZERO) > 0) {
                    f.setInfantAmount(data.getInfantPrice());
                }

                if (data.getTotalPrice() != null
                    && data.getTotalPrice().compareTo(BigDecimal.ZERO) > 0) {
                    f.setTotalAmount(data.getTotalPrice());
                }

                BookingFlightStatus flightStatus = BookingFlightStatus.fromBookingPartnerStatus(data.getStatus());
                f.setStatus(flightStatus);
                f.setOtaReference(data.getOtaReference());

                bookingFlightRepository.save(f);
                if(!flightStatus.equals(BookingFlightStatus.BOOKED)) {
                    continue;
                }

                bookingFlightHistoryRepository.save(
                    BookingFlightHistory.builder()
                        .bookingId(bookingId)
                        .status(flightStatus)
                        .data(data.toString())
                        .build()
                );
            }
        }

    }

}
