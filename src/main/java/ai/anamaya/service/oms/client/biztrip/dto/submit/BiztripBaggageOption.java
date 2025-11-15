package ai.anamaya.service.oms.client.biztrip.dto.submit;

import lombok.Data;

@Data
public class BiztripBaggageOption {
    private String id;
    private String baggageType;
    private String baggageQuantity;
    private String baggageWeight;
    private BiztripPrice priceWithCurrency;
    private BiztripPrice netToAgent;
}
