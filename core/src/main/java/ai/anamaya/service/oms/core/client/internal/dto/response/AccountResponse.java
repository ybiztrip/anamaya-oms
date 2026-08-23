package ai.anamaya.service.oms.core.client.internal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {

    private String id;
    private String status;
    private Long createdOn;
    private Long updatedOn;
    private String name;
    private String legalName;
}
