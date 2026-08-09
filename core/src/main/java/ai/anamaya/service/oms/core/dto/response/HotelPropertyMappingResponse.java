package ai.anamaya.service.oms.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelPropertyMappingResponse {

    private Long id;
    private Long propertyId;
    private String providerPropertyId;
    private String providerAliasName;
    private String provider;
    private String status;
    private Long createdOn;
    private Long updatedOn;
}
