package ai.anamaya.service.oms.rest.dto.request;

import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
import ai.anamaya.service.oms.core.enums.BookingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundAdminCreateRequestRest {

    @NotNull
    private BookingType bookingType;

    @NotBlank
    private String bookingCode;

    private String otaReference;

    @NotNull
    @Positive
    private BigDecimal requestedAmount;

    private String remarks;

    private Boolean isManualRefund;

    private Boolean isRequestCancel;

    @NotNull
    private BookingPaymentMethod refundType;
}
