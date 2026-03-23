package ai.anamaya.service.oms.core.client.biztrip.mapper.response;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripPaymentCCResponse;
import ai.anamaya.service.oms.core.dto.response.PaymentCCResponse;

public class BiztripPaymentCCResponseMapper {

    public PaymentCCResponse mapToResponse(BiztripPaymentCCResponse s) {
        return PaymentCCResponse.builder()
            .name(s.getName())
            .bank(s.getBank())
            .clientName(s.getClientName())
            .lastSixDigitCardNumber(s.getLastSixDigitCardNumber())
            .authentication(s.isAuthentication())
            .accountId(s.getAccountId())
            .build();
    }

}
