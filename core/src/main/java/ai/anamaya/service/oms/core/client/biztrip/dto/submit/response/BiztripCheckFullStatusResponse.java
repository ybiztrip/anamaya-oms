package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

import java.util.List;

@Data
public class BiztripCheckFullStatusResponse {
    private List<BiztripCheckFullStatusResult> bookingStatusResult;
}
