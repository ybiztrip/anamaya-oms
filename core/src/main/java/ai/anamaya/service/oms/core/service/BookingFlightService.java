package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.BookingFlightRequest;
import ai.anamaya.service.oms.core.dto.request.BookingFlightSubmitRequest;
import ai.anamaya.service.oms.core.dto.request.booking.submit.*;
import ai.anamaya.service.oms.core.dto.response.BookingResponse;
import ai.anamaya.service.oms.core.dto.response.booking.data.BookingDataResponse;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingFlightSubmitResponse;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.entity.BookingFlightHistory;
import ai.anamaya.service.oms.core.entity.BookingPax;
import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import ai.anamaya.service.oms.core.enums.BookingStatus;
import ai.anamaya.service.oms.core.enums.PaxType;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
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
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingFlightService {

    private final BookingFlightRepository bookingFlightRepository;
    private final BookingFlightHistoryRepository bookingFlightHistoryRepository;
    private final BookingPaxRepository bookingPaxRepository;
    private final BookingService bookingService;
    private final BookingPaxService bookingPaxService;

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

    @Transactional(rollbackFor = Exception.class)
    public BookingResponse submitBookingFlights(CallerContext callerContext, Long bookingId, BookingFlightSubmitRequest request) {
        Long userId = callerContext.userId();
        Booking booking = bookingService.getValidatedBooking(bookingId);

        if (!booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AccessDeniedException("This booking journey is not approved.");
        }

        List<BookingFlightRequest> requestFlights = request.getFlights();
        String bookingCode = "ANMF:"+ Instant.now().toEpochMilli();

        for (BookingFlightRequest req : requestFlights) {
            if (req.getDepartureDatetime().isBefore(booking.getStartDate().atStartOfDay())
                || req.getDepartureDatetime().isAfter(booking.getEndDate().atStartOfDay())
            ) {
                throw new IllegalArgumentException("Booking date flight is outside journey date");
            }
            BookingFlight newFlight = BookingFlight.builder()
                .bookingId(bookingId)
                .bookingCode(bookingCode)
                .type(req.getType())
                .clientSource(req.getClientSource())
                .itemId(req.getItemId())
                .origin(req.getOrigin())
                .destination(req.getDestination())
                .departureDatetime(req.getDepartureDatetime())
                .arrivalDatetime(req.getArrivalDatetime())
                .status(BookingFlightStatus.DRAFT)
                .createdBy(userId)
                .updatedBy(userId)
                .build();
            bookingFlightRepository.save(newFlight);
        }

        bookingPaxService.submitBookingPax(callerContext, bookingId, bookingCode, request.getPaxs());

        createBookingFLight(callerContext, booking, bookingCode);

        return bookingService.toResponse(booking, true, true);
    }

    private void createBookingFLight(CallerContext callerContext, Booking booking, String bookingCode) {
        List<BookingPax> pax = bookingPaxRepository.findByBookingIdAndBookingCode(booking.getId(), bookingCode);
        List<BookingFlight> flights = bookingFlightRepository.findByBookingIdAndBookingCode(booking.getId(), bookingCode);
        if(flights == null || flights.isEmpty()) {
            return;
        }

        FlightBookingSubmitRequest request = buildSubmitRequest(booking, pax, flights);

        FlightProvider provider = getProvider("biztrip");

        BookingFlightSubmitResponse response = provider.submitBooking(request);
        BookingFlightStatus bookingFlightStatus = BookingFlightStatus.fromBookingPartnerStatus(response.getBookingSubmissionStatus());
        bookingFlightHistoryRepository.save(
            BookingFlightHistory.builder()
                .bookingId(booking.getId())
                .bookingCode(bookingCode)
                .status(bookingFlightStatus)
                .data(response)
                .build()
        );

        if(bookingFlightStatus == BookingFlightStatus.CREATED) {
            booking.setStatus(BookingStatus.ON_PROCESS_CREATE);
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

        updateBookingFlightData(booking.getId(), bookingDataResponse);
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

}
