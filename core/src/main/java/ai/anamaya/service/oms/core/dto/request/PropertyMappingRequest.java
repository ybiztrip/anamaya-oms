package ai.anamaya.service.oms.core.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyMappingRequest {

    @NotEmpty(message = "providerPropertyId is required")
    private List<Long> providerPropertyId;

    private String providerAliasName;
}
