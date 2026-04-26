package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditAdjustRequest {
    private Long companyId;
    private CreditCodeType code;
    private CreditSourceType sourceType;
    private CreditTransactionType type;
    private BigDecimal amount;
    private Long referenceId;
    private String referenceCode;
    private String remarks;
}
