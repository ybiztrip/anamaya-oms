package ai.anamaya.service.oms.rest.dto.response;

import ai.anamaya.service.oms.core.enums.PaxTitle;
import ai.anamaya.service.oms.core.enums.PaxType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private String countryCode;
    private PaxType type;
    private String phoneNo;
    private PaxTitle title;
    private String identityNo;
    private String passportNo;
    private Date passportExpiry;
    private Date dateOfBirth;
    private String nationalityCode;
    private Short status;
    private Long createdBy;
    private String createdAt;
    private Long updatedBy;
    private String updatedAt;
}
