package ai.anamaya.service.oms.core.client.biztrip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiztripApiResponse<T> {
    private boolean success;
    private T data;
}