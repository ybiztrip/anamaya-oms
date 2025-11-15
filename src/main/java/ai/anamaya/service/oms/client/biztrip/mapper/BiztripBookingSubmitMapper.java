package ai.anamaya.service.oms.client.biztrip.mapper;

import ai.anamaya.service.oms.client.biztrip.dto.submit.*;
import ai.anamaya.service.oms.dto.response.booking.submit.*;

import java.util.List;

public class BiztripBookingSubmitMapper {

    public BookingSubmitResponse map(BiztripSubmitResponse b) {
        BookingSubmitResponse res = new BookingSubmitResponse();

        res.setBookingSubmissionStatus(b.getBookingSubmissionStatus());
        res.setBookingId(b.getBookingId());
        res.setPartnerBookingId(b.getPartnerBookingId());
        res.setPaymentExpirationTime(b.getPaymentExpirationTime());

        // Map flight booking detail
        FlightBookingDetail detail = new FlightBookingDetail();
        detail.setFareDetail(mapFare(b.getFlightBookingDetail().getFareDetail()));
        detail.setGrandTotalFareWithCurrency(mapPrice(b.getFlightBookingDetail().getGrandTotalFareWithCurrency()));
        detail.setPassengers(mapPassengers(b.getFlightBookingDetail().getPassengers()));
        detail.setJourneys(mapJourneys(b.getFlightBookingDetail().getJourneys()));

        res.setFlightBookingDetail(detail);

        return res;
    }

    private FareDetail mapFare(BiztripFareDetail fare) {
        if (fare == null) return null;

        FareDetail out = new FareDetail();
        out.setAdultFare(mapPrice(fare.getTotalFareWithCurrency()));

        // child fare might be null
        if (fare.getAdditionalFeeWithCurrency() != null)
            out.setChildFare(mapPrice(fare.getAdditionalFeeWithCurrency())); // optional

        // infant (Biztrip)
        // if Biztrip has infant fare detail, map here (Biztrip does not always provide)
        return out;
    }

    private Price mapPrice(BiztripPrice p) {
        if (p == null) return null;

        Price price = new Price();
        price.setAmount(Double.valueOf(p.getAmount()));
        price.setCurrency(p.getCurrency());
        return price;
    }

    private List<Passenger> mapPassengers(BiztripPassengers bp) {
        if (bp == null || bp.getAdults() == null) return List.of();

        return bp.getAdults().stream()
                .map(this::mapPassenger)
                .toList();
    }

    private Passenger mapPassenger(BiztripPassenger p) {
        Passenger out = new Passenger();
        out.setTitle(p.getTitle());
        out.setFirstName(p.getFirstName());
        out.setLastName(p.getLastName());
        out.setGender(p.getGender());
        return out;
    }

    private List<Journey> mapJourneys(List<BiztripJourney> journeys) {
        if (journeys == null) return List.of();

        return journeys.stream()
                .map(j -> {
                    Journey oj = new Journey();
                    oj.setSegments(
                            j.getSegments().stream()
                                    .map(this::mapSegment)
                                    .toList()
                    );
                    return oj;
                })
                .toList();
    }

    private Segment mapSegment(BiztripSegment s) {
        Segment seg = new Segment();

        seg.setFlightCode(s.getFlightCode());
        seg.setDepartureAirport(s.getDepartureDetail().getAirportCode());
        seg.setArrivalAirport(s.getArrivalDetail().getAirportCode());

        seg.setDepartureTime(
                s.getDepartureDetail().getDepartureDate() + " " + s.getDepartureDetail().getDepartureTime()
        );

        seg.setArrivalTime(
                s.getArrivalDetail().getArrivalDate() + " " + s.getArrivalDetail().getArrivalTime()
        );

        return seg;
    }
}
