package ai.anamaya.service.oms.rest.dto.request;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyConfigUpdateRequestRest {

    private String valueStr;
    private Integer valueInt;
    private Boolean valueBool;

    @AssertTrue(message = "At least one of valueStr, valueInt, or valueBool must be provided")
    public boolean isAnyValuePresent() {
        return valueStr != null || valueInt != null || valueBool != null;
    }
}
