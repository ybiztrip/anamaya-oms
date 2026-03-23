package ai.anamaya.service.oms.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCCResponseRest {

    private String name;
    private String bank;
    private String clientName;
    private String lastSixDigitCardNumber;
    private boolean authentication;
    private String accountId;

}

