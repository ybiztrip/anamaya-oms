package ai.anamaya.service.oms.rest.dto.response;

import ai.anamaya.service.oms.core.enums.BalanceSourceType;
import ai.anamaya.service.oms.core.enums.BalanceTransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CompanyBalanceDetailResponseRest {
    private Long id;
    private Long referenceId;
    private String referenceCode;
    private BalanceSourceType sourceType;
    private BalanceTransactionType type;
    private BigDecimal amount;
    private BigDecimal beginBalance;
    private BigDecimal endBalance;
    private String remarks;
    private String createdAt;
}
