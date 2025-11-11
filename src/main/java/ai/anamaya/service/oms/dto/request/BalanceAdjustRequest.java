package ai.anamaya.service.oms.dto.request;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceAdjustRequest {
    private String code;
    private Short sourceType;
    private Short type;
    private BigDecimal amount;
    private Long referenceId;
    private String referenceCode;
    private String remarks;
}
