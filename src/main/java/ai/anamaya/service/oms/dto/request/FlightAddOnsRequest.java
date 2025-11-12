package ai.anamaya.service.oms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightAddOnsRequest {
    private String journeyType;
    private List<String> flightIds;
}
