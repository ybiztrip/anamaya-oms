package ai.anamaya.service.oms.rest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGetListRequestRest {
    private Long userId;
    private Long companyId;
    private String email;
    private Short status;
}
