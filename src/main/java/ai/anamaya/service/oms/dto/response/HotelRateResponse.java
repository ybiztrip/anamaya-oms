package ai.anamaya.service.oms.dto.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelRateResponse {

    private String propertyId;
    private String rateStatus;
    private String roomId;
    private String roomName;
    private String roomType;
    private int numRooms;
    private int numAdults;
    private int numChildren;
    private int maxOccupancy;
    private String checkInDate;
    private String checkOutDate;
    private String mealType;
    private boolean refundable;
    private boolean smokingAllowed;
    private String rateKey;

    private Map<String, Object> totalRates;
    private Map<String, Object> nightlyRates;
    private List<Map<String, Object>> charges;
    private Map<String, Object> cancellationPolicy;
    private List<Map<String, Object>> bedGroups;
    private List<Map<String, Object>> facilityData;
}
