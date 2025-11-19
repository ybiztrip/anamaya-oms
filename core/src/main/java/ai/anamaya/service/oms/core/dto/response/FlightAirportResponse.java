package ai.anamaya.service.oms.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightAirportResponse {
    private String airportCode;
    private String city;
    private String countryId;
    private String countryCode;
    private String areaCode;
    private String timeZone;
    private String internationalAirportName;
    private String airportIcaoCode;
    private String localAirportName;
    private String localCityName;
    private String countryName;
}
