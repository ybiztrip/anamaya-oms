package ai.anamaya.service.oms.client.biztrip.dto.submit;

import lombok.Data;

import java.util.List;

@Data
public class BiztripPassengers {
    private List<BiztripPassenger> adults;
    private List<BiztripPassenger> children;
    private List<BiztripPassenger> infants;
}
