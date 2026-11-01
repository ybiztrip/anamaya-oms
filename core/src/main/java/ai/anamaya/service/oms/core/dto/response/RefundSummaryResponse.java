package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.RefundStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RefundSummaryResponse {
    private long total;
    private Map<RefundStatus, Long> status;
}
