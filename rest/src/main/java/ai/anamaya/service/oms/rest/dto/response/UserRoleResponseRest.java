package ai.anamaya.service.oms.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleResponseRest {
    private Long id;
    private Long roleId;
    private String roleName;
    private String roleCode;
}
