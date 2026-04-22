package ai.anamaya.service.oms.rest.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyConfigBatchUpdateItemRest {

    @NotBlank
    private String code;

    private String valueStr;
    private Integer valueInt;
    private Boolean valueBool;

    @AssertTrue(message = "At least one of valueStr, valueInt, or valueBool must be provided")
    public boolean isAnyValuePresent() {
        return valueStr != null || valueInt != null || valueBool != null;
    }
}
