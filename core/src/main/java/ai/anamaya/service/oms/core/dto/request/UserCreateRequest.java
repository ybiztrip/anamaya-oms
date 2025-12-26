package ai.anamaya.service.oms.core.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {

    private Long companyId;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String firstName;
    private String lastName;
    private String gender;
    private Long positionId;
    private String countryCode;
    private String phoneNo;

    @NotNull
    private Short status;

    private Boolean enableChatEngine;
}
