package ai.anamaya.service.oms.core.dto.response;

import lombok.Data;

@Data
public class BaseListResponse {
    private long totalElements;
    private int totalPages;
    private boolean last;
    private int size;
    private int number;
}
