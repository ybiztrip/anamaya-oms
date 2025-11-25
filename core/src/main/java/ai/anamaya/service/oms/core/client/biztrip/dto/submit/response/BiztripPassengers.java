package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

import java.util.List;

@Data
public class BiztripPassengers {
    private List<BiztripPassenger> adults;
    private List<BiztripPassenger> children;
    private List<BiztripPassenger> infants;
}
