package ai.anamaya.service.oms.core.client.internal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderSaveRequest {

    @NotBlank(message = "accountId is required")
    private String accountId;

    @NotBlank(message = "provider is required")
    private String provider;

    @NotBlank(message = "category is required")
    private String category;

    @NotBlank(message = "status is required")
    private String status;
}
