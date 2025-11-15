package ai.anamaya.service.oms.client.biztrip.dto.submit;

import lombok.Data;

@Data
public class BiztripNetToAgent {
    private BiztripPrice adultFare;
    private BiztripPrice childFare;
    private BiztripPrice infantFare;
}
