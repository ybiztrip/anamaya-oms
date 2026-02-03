package ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BiztripHotelRoomRateResponse {

    // ===== BASIC INFO =====
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
    private Integer maxOccupancy;

    private String mealType;
    private String rateKey;

    // ===== FLAGS =====
    private Boolean refundable;
    private Boolean isRefundable;
    private Boolean wifiIncluded;
    private Boolean breakfastIncluded;
    private Boolean smokingAllowed;

    // ===== PRICING =====
    private TotalRates totalRates;
    private NightlyRates nightlyRates;
    private List<Charge> charges;

    private List<RatesPerDay> ratesPerDay;

    // ===== POLICIES =====
    private Map<String, Object> cancellationPolicy;
    private Map<String, Object> checkInPolicy;
    private Map<String, Object> occupancyPricing;

    // ===== ROOM DETAILS =====
    private List<Map<String, Object>> bedArrangement;
    private List<Map<String, Object>> roomImages;
    private List<Map<String, Object>> roomFacilities;
    private Map<String, Object> roomSize;

    // ===== PROPERTY DETAILS =====
    private Map<String, Object> propertySummary;
    private List<Map<String, Object>> propertyImages;
    private List<Map<String, Object>> propertyFacilities;

    // ================= INNER CLASSES =================

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

    @Data
    public static class RatesPerDay {
        private String date;
        private String baseRate;
        private Boolean promo;
        private String nightRate;
    }
}
