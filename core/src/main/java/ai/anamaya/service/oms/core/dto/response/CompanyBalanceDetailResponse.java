package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.BalanceSourceType;
import ai.anamaya.service.oms.core.enums.BalanceTransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyBalanceDetailResponse {
    private Long id;
    private Long referenceId;
    private String referenceCode;
    private BalanceSourceType sourceType;
    private BalanceTransactionType type;
    private BigDecimal amount;
    private BigDecimal beginBalance;
    private BigDecimal endBalance;
    private String remarks;
    private LocalDateTime createdAt;
}
