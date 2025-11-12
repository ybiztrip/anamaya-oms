package ai.anamaya.service.oms.dto.response;

import lombok.Data;

@Data
public class BaggageOptionResponse {
    private String id;
    private String baggageType;
    private String baggageQuantity;
    private String baggageWeight;
    private PriceResponse priceWithCurrency;
    private PriceResponse netToAgent;
}
