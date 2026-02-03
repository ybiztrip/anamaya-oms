package ai.anamaya.service.oms.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelRoomRateResponse {

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

    // ===== PRICING =====
    private Map<String, Object> totalRates;
    private Map<String, Object> nightlyRates;
    private List<Map<String, Object>> charges;
    private List<Map<String, Object>> ratesPerDay;

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

    // ===== FLAGS =====
    private Boolean refundable;
    private Boolean isRefundable;
    private Boolean wifiIncluded;
    private Boolean breakfastIncluded;
    private Boolean smokingAllowed;
}
