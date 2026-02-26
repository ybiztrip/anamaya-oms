package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.PaxTitle;
import ai.anamaya.service.oms.core.enums.PaxType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
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
