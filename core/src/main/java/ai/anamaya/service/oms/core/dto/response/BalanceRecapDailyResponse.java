package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.BalanceCodeType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class BalanceRecapDailyResponse {

    private Long id;
    private Long companyId;
    private BalanceCodeType code;
    private LocalDate recapDate;
    private BigDecimal beginBalance;
    private BigDecimal endBalance;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}