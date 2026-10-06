package ai.anamaya.service.oms.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainStationResponse {
    private String stationName;
    private String stationCode;
    private String cityName;
}
