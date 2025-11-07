package ai.anamaya.service.oms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse {
    private String id;
    private String status;
    private String name;
    private Double latitude;
    private Double longitude;
    private List<String> lineData;
    private String city;
    private String province;
    private String postalCode;
    private String country;
    private Integer star;
    private String accommodationType;
    private List<Map<String, Object>> propertyImageData;
    private List<Map<String, Object>> facilityData;
}
