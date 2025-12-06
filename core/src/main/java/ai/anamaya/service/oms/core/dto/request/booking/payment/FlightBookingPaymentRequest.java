package ai.anamaya.service.oms.core.dto.request.booking.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightBookingPaymentRequest {
    private String bookingId;
    private String paymentMethod;
}
