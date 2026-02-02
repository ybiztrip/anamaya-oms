package ai.anamaya.service.oms.core.client.biztrip.dto.flight.response;

import lombok.Data;

@Data
public class BiztripFlightBaggageOption {
    private String id;
    private String baggageType;
    private String baggageQuantity;
    private String baggageWeight;
    private BiztripFlightPrice priceWithCurrency;
    private BiztripFlightPrice netToAgent;
}
