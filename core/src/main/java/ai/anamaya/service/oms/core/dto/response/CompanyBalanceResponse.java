package ai.anamaya.service.oms.core.dto.response;

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
    private String code;
    private BigDecimal balance;
    private String currency;
    private Short status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
