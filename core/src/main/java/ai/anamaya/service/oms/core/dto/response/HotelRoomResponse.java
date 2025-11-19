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
public class HotelRoomResponse {

    private String roomId;
    private String propertyId;
    private String roomStatus;
    private String roomName;
    private String roomType;
    private List<Map<String, Object>> bedArrangementData;
    private List<Map<String, Object>> imageData;
    private List<Map<String, Object>> facilityData;
    private String roomView;
    private Boolean roomWindow;
    private String size;
    private String unit;
}
