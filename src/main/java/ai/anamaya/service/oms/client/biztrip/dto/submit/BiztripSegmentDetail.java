package ai.anamaya.service.oms.client.biztrip.dto.submit;

import lombok.Data;

@Data
public class BiztripSegmentDetail {
    private String airportCode;
    private String departureDate;
    private String departureTime;
    private String departureTerminal;
    private String arrivalDate;
    private String arrivalTime;
    private String arrivalTerminal;
}
