package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.BalanceCodeType;
import ai.anamaya.service.oms.core.enums.CreditCodeType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CompanyCreditInvoiceRequest {
    private CreditCodeType code;
    private String docNo;
    private BigDecimal amount;
}
