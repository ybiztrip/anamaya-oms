package ai.anamaya.service.oms.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyConfigBatchUpdateItem {
    private String code;
    private String valueStr;
    private Integer valueInt;
    private Boolean valueBool;
}
