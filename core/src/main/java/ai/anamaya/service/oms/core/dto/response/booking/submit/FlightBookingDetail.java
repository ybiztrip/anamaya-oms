package ai.anamaya.service.oms.core.dto.response.booking.submit;

import lombok.Data;

import java.util.List;

@Data
public class FlightBookingDetail {
    private FareDetail fareDetail;
    private List<Passenger> passengers;
    private List<Journey> journeys;
    private Price grandTotalFareWithCurrency;
}
