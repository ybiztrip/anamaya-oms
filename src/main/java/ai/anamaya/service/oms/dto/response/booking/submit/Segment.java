package ai.anamaya.service.oms.dto.response.booking.submit;

import lombok.Data;

@Data
public class Segment {
    private String flightCode;
    private String departureAirport;
    private String arrivalAirport;
    private String departureTime;
    private String arrivalTime;
}
