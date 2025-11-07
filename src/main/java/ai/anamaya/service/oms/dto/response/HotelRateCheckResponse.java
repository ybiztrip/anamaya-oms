package ai.anamaya.service.oms.dto.response;

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
public class HotelRateCheckResponse {

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
    private String mealType;

    private Map<String, Object> totalRates;
    private Map<String, Object> nightlyRates;
    private List<Map<String, Object>> charges;
    private Map<String, Object> cancellationPolicy;
    private Map<String, Object> occupancyPricing;

    private Boolean refundable;
    private Boolean isRefundable;
}
