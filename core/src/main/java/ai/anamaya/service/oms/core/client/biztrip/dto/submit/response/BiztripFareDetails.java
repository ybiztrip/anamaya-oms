package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

@Data
public class BiztripFareDetails {
    private BiztripFareDetail adultFare;
    private BiztripFareDetail childFare;
    private BiztripFareDetail infantFare;
}
