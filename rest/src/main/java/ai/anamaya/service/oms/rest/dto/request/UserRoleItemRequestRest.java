package ai.anamaya.service.oms.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleItemRequestRest {

    @NotNull(message = "Role ID is required")
    private Long roleId;

    @NotNull(message = "isDelete is required")
    private Boolean isDelete;
}
