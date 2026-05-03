package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.CreditCodeType;
import ai.anamaya.service.oms.core.enums.InvoiceStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CompanyCreditInvoiceResponse {
    private Long id;
    private Long companyId;
    private CreditCodeType code;
    private String docNo;
    private BigDecimal amount;
    private String currency;
    private InvoiceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
