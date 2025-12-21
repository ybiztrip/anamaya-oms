package ai.anamaya.service.oms.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginExternalRequestRest {

    @NotBlank
    private String phoneNo;

    @NotBlank
    private String type;

    @NotBlank
    private String secret;

}
