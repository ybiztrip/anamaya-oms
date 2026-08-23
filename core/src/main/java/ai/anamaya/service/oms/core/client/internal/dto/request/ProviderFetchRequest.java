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
public class ProviderFetchRequest {

    @NotBlank(message = "accountId is required")
    private String accountId;

    @NotBlank(message = "category is required")
    private String category;
}
