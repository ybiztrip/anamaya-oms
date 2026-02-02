package ai.anamaya.service.oms.core.client.biztrip.dto.flight.response;

import lombok.Data;

import java.util.List;

@Data
public class BiztripFlightAddOnsResponse {
    private List<BiztripFlightAddOnDataResponse> journeysWithAvailableAddOnsOptions;
}
