package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.BalanceCodeType;
import ai.anamaya.service.oms.core.enums.BalanceSourceType;
import ai.anamaya.service.oms.core.enums.BalanceTransactionType;
import ai.anamaya.service.oms.core.enums.BookingType;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceAdjustRequest {
    private Long companyId;
    private BalanceCodeType code;
    private BalanceSourceType sourceType;
    private BookingType bookingType;
    private BalanceTransactionType type;
    private BigDecimal amount;
    private Long referenceId;
    private String referenceCode;
    private String contactEmail;
    private String triggeredByEmail;
    private String remarks;
}
