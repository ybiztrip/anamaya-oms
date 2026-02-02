package ai.anamaya.service.oms.core.client.biztrip.dto.flight.response;

import lombok.Data;

import java.util.List;

@Data
public class BiztripFlightAddOnDataResponse {
    private List<Object> segmentsWithAvailableAddOns;
    private BiztripFlightAvailableAddOnsOptions availableAddOnsOptions;
}
