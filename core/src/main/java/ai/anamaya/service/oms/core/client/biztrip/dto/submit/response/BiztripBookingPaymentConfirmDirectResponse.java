package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

@Data
public class BiztripBookingPaymentConfirmDirectResponse {
    private String bookingId;
    private String paymentConfirmationStatus;
}
