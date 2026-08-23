package ai.anamaya.service.oms.core.client.internal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderResponse {

    private String accountId;
    private String provider;
    private String category;
    private String status;
}
