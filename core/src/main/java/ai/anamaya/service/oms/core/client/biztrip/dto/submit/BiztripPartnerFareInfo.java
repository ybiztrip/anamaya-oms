package ai.anamaya.service.oms.core.client.biztrip.dto.submit;

import lombok.Data;

@Data
public class BiztripPartnerFareInfo {
    private BiztripPartnerFare partnerFare;
    private BiztripPartnerFare airlineFare;
    private BiztripNetToAgent netToAgent;
}
