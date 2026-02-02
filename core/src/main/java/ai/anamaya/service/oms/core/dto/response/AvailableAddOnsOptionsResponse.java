package ai.anamaya.service.oms.core.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class AvailableAddOnsOptionsResponse {
    private List<BaggageOptionResponse> baggageOptions;
    private List<Object> mealOptions;
}
