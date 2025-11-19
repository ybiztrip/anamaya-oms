package ai.anamaya.service.oms.core.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceTopUpRequest {

    @NotBlank
    private String code;

    @NotNull
    @DecimalMin(value = "0.01", message = "Top-up amount must be greater than zero")
    private BigDecimal amount;

    @NotNull
    private Short sourceType;

    private Long referenceId;
    private String referenceCode;
    private String remarks;
}
