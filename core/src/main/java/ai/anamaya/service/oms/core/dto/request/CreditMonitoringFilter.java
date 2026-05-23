package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.BookingType;
import ai.anamaya.service.oms.core.enums.CreditCodeType;
import ai.anamaya.service.oms.core.enums.CreditSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditMonitoringFilter {
    private Long companyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private CreditCodeType creditCodeType;
    private CreditSourceType sourceType;
    private BookingType bookingType;
}
