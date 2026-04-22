package ai.anamaya.service.oms.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyConfigBatchUpdateRequestRest {

    @NotEmpty
    @Valid
    private List<CompanyConfigBatchUpdateItemRest> items;
}
