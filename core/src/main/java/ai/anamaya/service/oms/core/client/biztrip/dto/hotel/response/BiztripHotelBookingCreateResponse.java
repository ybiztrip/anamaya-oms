package ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response;

import lombok.Data;

@Data
public class BiztripHotelBookingCreateResponse {

    private String bookingId;
    private String partnerBookingId;
    private String bookingStatus;
    private String propertyId;
    private String checkInDate;
    private String checkOutDate;
    private TotalRates totalChargeableRate;

    @Data
    public static class TotalRates {
        private String currencyCode;
        private String amount;
    }
}

