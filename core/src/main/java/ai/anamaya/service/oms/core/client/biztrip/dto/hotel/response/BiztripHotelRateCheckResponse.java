package ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response;

import lombok.Data;
import java.util.List;

@Data
public class BiztripHotelRateCheckResponse {

    private String rateStatus;
    private String propertyId;
    private String providerRoomId;
    private String roomId;
    private String roomName;
    private String roomType;
    private String checkInDate;
    private String checkOutDate;
    private Integer numRooms;
    private Integer numAdults;
    private Integer numChildren;
    private Boolean refundable;
    private Boolean isRefundable;
    private String rateKey;

    private TotalRates totalRates;
    private NightlyRates nightlyRates;
    private List<Charge> charges;

    @Data
    public static class TotalRates {
        private String displayCurrency;
        private Long displaySellAmount;
        private Long displayNettAmount;
        private String partnerCurrency;
        private Long partnerSellAmount;
        private Long partnerNettAmount;
    }

    @Data
    public static class NightlyRates {
        private String displayCurrency;
        private Long displaySellAmount;
        private Long displayNettAmount;
        private String partnerCurrency;
        private Long partnerSellAmount;
        private Long partnerNettAmount;
    }

    @Data
    public static class Charge {
        private String type;
        private String displayCurrency;
        private Long displayAmount;
        private Boolean included;
    }
}
