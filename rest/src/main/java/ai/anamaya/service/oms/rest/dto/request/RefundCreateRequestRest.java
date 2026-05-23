package ai.anamaya.service.oms.rest.dto.request;

import ai.anamaya.service.oms.core.enums.BookingType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundCreateRequestRest {
    private String code;

    @NotNull
    private BookingType bookingType;

    private Long bookingFlightId;
    private Long bookingHotelId;

    @NotNull
    @Positive
    private BigDecimal requestedAmount;

    private String remarks;
}
