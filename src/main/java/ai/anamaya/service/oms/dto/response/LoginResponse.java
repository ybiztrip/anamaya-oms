package ai.anamaya.service.oms.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String token;
}
