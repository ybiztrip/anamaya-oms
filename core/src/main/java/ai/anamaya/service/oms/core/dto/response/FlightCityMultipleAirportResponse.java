package ai.anamaya.service.oms.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightCityMultipleAirportResponse {
    private String city;
    private String cityCode;
    private List<String> airports;
    private String country;
}
