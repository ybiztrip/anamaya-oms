package ai.anamaya.service.oms.client.biztrip.dto.submit;

import lombok.Data;

import java.util.List;

@Data
public class BiztripAddOn {
    private List<Object> segmentsWithAvailableAddOns;
    private BiztripAvailableAddOnsOptions availableAddOnsOptions;
}
