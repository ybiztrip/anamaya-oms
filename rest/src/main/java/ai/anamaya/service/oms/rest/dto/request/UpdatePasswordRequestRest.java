package ai.anamaya.service.oms.rest.dto.request;

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
public class UpdatePasswordRequestRest {

    @NotNull
    private Long userId;

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
