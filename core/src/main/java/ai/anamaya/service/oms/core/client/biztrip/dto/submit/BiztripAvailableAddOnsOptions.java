package ai.anamaya.service.oms.core.client.biztrip.dto.submit;

import lombok.Data;

import java.util.List;

@Data
public class BiztripAvailableAddOnsOptions {
    private List<BiztripBaggageOption> baggageOptions;
    private List<Object> mealOptions;
}
