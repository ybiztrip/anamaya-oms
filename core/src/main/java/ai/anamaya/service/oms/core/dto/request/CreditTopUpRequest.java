package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.CreditCodeType;
import ai.anamaya.service.oms.core.enums.CreditSourceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditTopUpRequest {

    @NotBlank
    private CreditCodeType code;

    @NotNull
    @DecimalMin(value = "0.01", message = "Top-up amount must be greater than zero")
    private BigDecimal amount;

    @NotNull
    private CreditSourceType sourceType;

    private Long referenceId;
    private String referenceCode;
    private String remarks;
}
