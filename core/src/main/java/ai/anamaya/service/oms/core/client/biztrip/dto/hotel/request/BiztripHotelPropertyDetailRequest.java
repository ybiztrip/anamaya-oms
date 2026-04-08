package ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiztripHotelPropertyDetailRequest {
    private List<String> propertyIds;
}
