package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
import ai.anamaya.service.oms.core.enums.BookingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundAdminCreateRequest {
    private BookingType bookingType;
    private String bookingCode;
    private String otaReference;
    private BigDecimal requestedAmount;
    private String remarks;
    private Boolean isManualRefund;
    private Boolean isRequestCancel;
    private BookingPaymentMethod refundType;
}
