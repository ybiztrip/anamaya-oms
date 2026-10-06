package ai.anamaya.service.oms.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainAvailabilityResponse {
    // ponytail: schedules passed through raw like flight oneWayFlightSearchResults; type it out if a consumer needs the nested fields
    private List<Map<String, Object>> schedules;
}
