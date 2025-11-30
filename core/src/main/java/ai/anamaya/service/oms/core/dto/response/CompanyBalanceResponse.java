package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.BalanceCodeType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyBalanceResponse {
    private Long id;
    private Long companyId;
    private BalanceCodeType code;
    private BigDecimal balance;
    private String currency;
    private Short status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
