package ai.anamaya.service.oms.core.client.internal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyMappingRequest {

    @NotBlank(message = "provider is required")
    private String provider;

    @NotNull(message = "providerPropertyId is required")
    private Long providerPropertyId;

    private String providerAliasName;
}
