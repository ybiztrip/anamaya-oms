package ai.anamaya.service.oms.rest.dto.request;

import ai.anamaya.service.oms.core.enums.BalanceCodeType;
import ai.anamaya.service.oms.core.enums.BalanceSourceType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceTopUpRequestRest {
    private BalanceCodeType code;
    private BigDecimal amount;
    private BalanceSourceType sourceType;
    private Long referenceId;
    private String referenceCode;
    private String remarks;
}
