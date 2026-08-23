package ai.anamaya.service.oms.core.client.internal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelPropertyMappingUpdateResponse {

    private Integer updatedCount;
}
