package ai.anamaya.service.oms.core.client.biztrip.mapper.response;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.*;
import ai.anamaya.service.oms.core.dto.response.booking.submit.*;

import java.util.List;

public class BiztripBookingSubmitResponseMapper {

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

    private FareDetail mapFare(BiztripFareDetails fare) {
        if (fare == null) return null;

        FareDetail out = new FareDetail();
        if(fare.getAdultFare() != null) {
            out.setAdultFare(mapPrice(fare.getAdultFare().getTotalFareWithCurrency()));
        }

        if(fare.getChildFare() != null) {
            out.setChildFare(mapPrice(fare.getChildFare().getTotalFareWithCurrency()));
        }

        if(fare.getInfantFare() != null) {
            out.setChildFare(mapPrice(fare.getInfantFare().getTotalFareWithCurrency()));
        }

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
