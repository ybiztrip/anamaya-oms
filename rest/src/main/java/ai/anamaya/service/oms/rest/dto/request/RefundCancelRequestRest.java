package ai.anamaya.service.oms.rest.dto.request;

import ai.anamaya.service.oms.core.enums.BookingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefundCancelRequestRest {
    @NotNull
    private BookingType type;

    @NotBlank
    private String partnerBookingId;

    @NotBlank
    private String bookingId;

    private String remarks;
}
