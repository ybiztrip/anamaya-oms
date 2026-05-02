package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.ActivityLogType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLogListFilter {
    private int page;
    private int size;
    private String sort;
    private Long referenceId;
    private ActivityLogType type;
    private Long companyId;
}
