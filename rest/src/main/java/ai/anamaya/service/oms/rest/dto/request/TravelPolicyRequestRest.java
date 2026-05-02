package ai.anamaya.service.oms.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class TravelPolicyRequestRest {
    private Long companyId;

    @NotBlank
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
}
