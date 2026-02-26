package ai.anamaya.service.oms.rest.dto.request;

import ai.anamaya.service.oms.core.enums.PaxTitle;
import ai.anamaya.service.oms.core.enums.PaxType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestRest {

    @NotNull
    private Long companyId;

    @Email
    @NotBlank
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

    @NotNull
    private Short status;
}
