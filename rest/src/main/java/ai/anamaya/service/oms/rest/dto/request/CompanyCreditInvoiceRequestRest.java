package ai.anamaya.service.oms.rest.dto.request;

import ai.anamaya.service.oms.core.enums.CreditCodeType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CompanyCreditInvoiceRequestRest {
    private CreditCodeType code;
    private String docNo;
    private BigDecimal amount;
}
