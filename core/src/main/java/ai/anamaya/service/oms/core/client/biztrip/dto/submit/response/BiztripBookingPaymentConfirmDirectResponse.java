package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

@Data
public class BiztripBookingPaymentConfirmDirectResponse {
    private String bookingId;
    private String paymentConfirmationStatus;
    private CCChargeDetail ccChargeDetail;

    @Data
    public static class CCChargeDetail {
        private String ccPaymentUrl;
        private String baseTransactionAmount;
        private String transactionFee;
        private String totalTransactionAmount;
    }
}
