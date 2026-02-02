package ai.anamaya.service.oms.core.client.biztrip.dto.flight.response;

import lombok.Data;

import java.util.List;

@Data
public class BiztripFlightAvailableAddOnsOptions {
    private List<BiztripFlightBaggageOption> baggageOptions;
    private List<Object> mealOptions;
}
