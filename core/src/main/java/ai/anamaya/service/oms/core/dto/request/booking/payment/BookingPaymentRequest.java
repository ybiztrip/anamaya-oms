package ai.anamaya.service.oms.core.dto.request.booking.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingPaymentRequest {
    private String bookingId;
    private String paymentMethod;
}
