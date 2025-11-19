package ai.anamaya.service.oms.core.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class AvailableAddOnsOptionsResponse {
    private List<BaggageOptionResponse> baggageOptionResponses;
    private List<Object> mealOptions;
}
