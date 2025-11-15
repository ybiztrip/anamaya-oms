package ai.anamaya.service.oms.client.biztrip.dto.submit;

import lombok.Data;

@Data
public class BiztripPartnerFare {
    private BiztripFareDetail adultFare;
    private BiztripFareDetail childFare;
    private BiztripFareDetail infantFare;
}
