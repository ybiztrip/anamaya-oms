package ai.anamaya.service.oms.rest.dto.response;

import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
import ai.anamaya.service.oms.core.enums.BookingType;
import ai.anamaya.service.oms.core.enums.RefundStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class RefundResponseRest {
    private Long id;
    private Long companyId;
    private String code;
    private BookingType bookingType;
    private String bookingCode;
    private BookingPaymentMethod paymentMethod;
    private BigDecimal requestedAmount;
    private BigDecimal paidAmount;
    private String currency;
    private RefundStatus status;
    private String remarks;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
