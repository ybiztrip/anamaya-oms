package ai.anamaya.service.oms.core.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class JourneyAddOnsResponse {
    private List<Object> segmentsWithAvailableAddOns;
    private AvailableAddOnsOptionsResponse availableAddOnsOptions;
}
