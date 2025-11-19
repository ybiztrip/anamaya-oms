package ai.anamaya.service.oms.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseRest {
    private Long id;
    private Long companyId;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private Long positionId;
    private String phoneNo;
    private Short status;
    private Long createdBy;
    private String createdAt;
    private Long updatedBy;
    private String updatedAt;
}
