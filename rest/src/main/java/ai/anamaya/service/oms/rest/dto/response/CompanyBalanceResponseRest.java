package ai.anamaya.service.oms.rest.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CompanyBalanceResponseRest {
    private Long id;
    private Long companyId;
    private String code;
    private BigDecimal balance;
    private String currency;
    private Short status;
    private String createdAt;
    private String updatedAt;
}
