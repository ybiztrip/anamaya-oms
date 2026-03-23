package ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiztripPaymentCCResponse {

    private String name;
    private String bank;
    private String clientName;
    private String lastSixDigitCardNumber;
    private boolean authentication;
    private String accountId;

}
