package ai.anamaya.service.oms.core.client.internal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelPriceConfigResponse {

    private String accountId;
    private Double priceReduction;
    private Double priceAmplifier;
    private Double additionalFixedPrice;
    private String status;
}
