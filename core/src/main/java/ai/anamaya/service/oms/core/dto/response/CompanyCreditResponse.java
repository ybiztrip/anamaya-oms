package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.CreditCodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCreditResponse {
    private Long id;
    private Long companyId;
    private CreditCodeType code;
    private BigDecimal balance;
    private String currency;
    private Short status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
