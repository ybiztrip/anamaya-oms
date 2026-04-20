package ai.anamaya.service.oms.core.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyConfigResponse {
    private Long id;
    private Long companyId;
    private String code;
    private String valueStr;
    private Integer valueInt;
    private Boolean valueBool;
    private Short status;
    private Short isVisible;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}
