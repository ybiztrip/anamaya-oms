package ai.anamaya.service.oms.rest.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class BalanceRecapDailyResponseRest {

    private Long id;
    private Long companyId;
    private String code;
    private LocalDate recapDate;
    private BigDecimal beginBalance;
    private BigDecimal endBalance;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}