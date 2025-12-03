package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

@Data
public class BiztripBaseResponse<T> {
    private boolean success;
    private T data;
}
