package ai.anamaya.service.oms.rest.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequestRest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
