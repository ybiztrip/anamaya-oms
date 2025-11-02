package ai.anamaya.service.oms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePasswordRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
