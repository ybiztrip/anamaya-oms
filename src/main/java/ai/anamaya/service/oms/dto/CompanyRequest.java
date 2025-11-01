package ai.anamaya.service.oms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRequest {

    @NotBlank
    private String name;

    @NotNull
    private Short status;

    @NotNull
    private Long createdBy;
}
