package ai.anamaya.service.oms.rest.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceTopUpRequestRest {
    private String code;
    private BigDecimal amount;
    private short sourceType;
    private Long referenceId;
    private String referenceCode;
    private String remarks;
}
