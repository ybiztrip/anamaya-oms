package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.PaxTitle;
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
public class UserUpdateRequest {

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
    private String phoneNo;
    private PaxTitle title;
    private String identityNo;
    private String passportNo;
    private Date passportExpiry;
    private Date dateOfBirth;

    @NotNull
    private Short status;
}
