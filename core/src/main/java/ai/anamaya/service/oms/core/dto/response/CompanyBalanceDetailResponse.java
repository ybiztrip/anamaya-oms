package ai.anamaya.service.oms.core.dto.response;

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
    private Short sourceType;
    private Short type;
    private BigDecimal amount;
    private BigDecimal beginBalance;
    private BigDecimal endBalance;
    private String remarks;
    private LocalDateTime createdAt;
}
