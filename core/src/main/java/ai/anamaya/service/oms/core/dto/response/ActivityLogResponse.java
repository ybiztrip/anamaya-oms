package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.ActivityLogType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityLogResponse {
    private Long id;
    private Long companyId;
    private ActivityLogType type;
    private Long referenceId;
    private JsonNode data;
    private JsonNode changeSummary;
    private Short status;
    private Long createdBy;
    private String createdAt;
    private Long updatedBy;
    private String updatedAt;
}
