package ai.anamaya.service.oms.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundPaidRequestRest {
    @NotNull
    @Positive
    private BigDecimal paidAmount;

    private String remarks;
}
