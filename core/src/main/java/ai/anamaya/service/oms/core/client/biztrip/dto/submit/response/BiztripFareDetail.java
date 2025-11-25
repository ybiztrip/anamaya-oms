package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

@Data
public class BiztripFareDetail {
    private BiztripPrice baseFareWithCurrency;
    private BiztripPrice vatWithCurrency;
    private BiztripPrice pscWithCurrency;
    private BiztripPrice fuelSurchargeWithCurrency;
    private BiztripPrice adminFeeWithCurrency;
    private BiztripPrice additionalFeeWithCurrency;
    private BiztripPrice totalFareWithCurrency;
}
