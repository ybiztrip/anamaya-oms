package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
import ai.anamaya.service.oms.core.enums.BookingType;
import ai.anamaya.service.oms.core.enums.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundFilter {
    private Long companyId;
    private BookingType bookingType;
    private RefundStatus status;
    private BookingPaymentMethod paymentMethod;
    private String code;
    private String bookingCode;
    private LocalDate startDate;
    private LocalDate endDate;
}
