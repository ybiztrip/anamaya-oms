package ai.anamaya.service.oms.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String phoneNo;

    @NotNull
    private Short status;

    @NotNull
    private Long createdBy;
}
