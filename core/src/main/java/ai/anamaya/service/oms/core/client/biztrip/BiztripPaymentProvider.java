package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.PaymentCCListRequest;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.service.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("biztripPaymentProvider")
@RequiredArgsConstructor
public class BiztripPaymentProvider implements PaymentProvider {

    private final BiztripPaymentGetCCService biztripPaymentGetCCService;

    @Override
    public List<PaymentCCResponse> getCC(CallerContext callerContext, PaymentCCListRequest request) {
        return biztripPaymentGetCCService.getCC(callerContext, request);
    }

}
