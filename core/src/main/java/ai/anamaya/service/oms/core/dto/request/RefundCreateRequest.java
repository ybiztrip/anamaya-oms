package ai.anamaya.service.oms.core.dto.request;

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
public class RefundCreateRequest {
    private String code;
    private BookingType bookingType;
    private Long bookingFlightId;
    private Long bookingHotelId;
    private BigDecimal requestedAmount;
    private String remarks;
}
