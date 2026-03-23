package ai.anamaya.service.oms.core.dto.request.booking.payment;

import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
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
    private BookingPaymentMethod paymentMethod;
    private CreditCardDetail creditCardDetail;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreditCardDetail {
        private String lastSixDigitNumber;
        private String cardName;
    }
}
