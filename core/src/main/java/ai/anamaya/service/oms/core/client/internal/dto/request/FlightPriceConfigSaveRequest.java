package ai.anamaya.service.oms.core.client.internal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightPriceConfigSaveRequest {

    @NotBlank(message = "accountId is required")
    private String accountId;

    @NotNull(message = "priceReduction is required")
    private Double priceReduction;

    @NotNull(message = "priceAmplifier is required")
    private Double priceAmplifier;

    @NotNull(message = "additionalFixedPrice is required")
    private Double additionalFixedPrice;

    @NotBlank(message = "status is required")
    private String status;
}
