package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.BalanceCodeType;
import ai.anamaya.service.oms.core.enums.BalanceSourceType;
import ai.anamaya.service.oms.core.enums.BookingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceMonitoringFilter {
    private Long companyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BalanceCodeType balanceCodeType;
    private BalanceSourceType sourceType;
    private BookingType bookingType;
    private String referenceCode;
    private String contactEmail;
}
