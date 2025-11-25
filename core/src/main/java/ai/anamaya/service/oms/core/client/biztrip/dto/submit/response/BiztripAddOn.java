package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

import java.util.List;

@Data
public class BiztripAddOn {
    private List<Object> segmentsWithAvailableAddOns;
    private BiztripAvailableAddOnsOptions availableAddOnsOptions;
}
