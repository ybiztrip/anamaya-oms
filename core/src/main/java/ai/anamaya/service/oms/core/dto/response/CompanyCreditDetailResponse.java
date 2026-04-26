package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.CreditSourceType;
import ai.anamaya.service.oms.core.enums.CreditTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCreditDetailResponse {
    private Long id;
    private Long referenceId;
    private String referenceCode;
    private CreditSourceType sourceType;
    private CreditTransactionType type;
    private BigDecimal amount;
    private BigDecimal beginBalance;
    private BigDecimal endBalance;
    private String remarks;
    private LocalDateTime createdAt;
}
