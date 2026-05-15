package ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response;

import lombok.Data;

@Data
public class BiztripHotelBookingCreateResponse {

    private String bookingId;
    private String partnerBookingId;
    private String bookingStatus;
    private String itineraryId;
    private String propertyId;
    private String checkInDate;
    private String checkOutDate;
    private TotalRates totalChargeableRate;
    private CCChargeDetail ccChargeDetail;

    @Data
    public static class TotalRates {
        private String currencyCode;
        private String amount;
    }

    @Data
    public static class CCChargeDetail {
        private String ccPaymentUrl;
        private String baseTransactionAmount;
        private String transactionFee;
        private String totalTransactionAmount;
    }
}

