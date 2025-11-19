package ai.anamaya.service.oms.rest.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseRest {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String token;
}
