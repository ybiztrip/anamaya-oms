package ai.anamaya.service.oms.core.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TravelPolicyResponse {
    private Long id;
    private Long companyId;
    private String name;
    private List<Map<String, Object>> flights;
    private Integer flightMinimumPrice;
    private Integer flightMaximumPrice;
    private String flightMinimumClass;
    private String flightMaximumClass;
    private Integer hotelMinimumPrice;
    private Integer hotelMaximumPrice;
    private String hotelMinimumClass;
    private String hotelMaximumClass;
    private String hotelPagu;
    private Short status;
    private Long createdBy;
    private String createdAt;
    private Long updatedBy;
    private String updatedAt;
}
