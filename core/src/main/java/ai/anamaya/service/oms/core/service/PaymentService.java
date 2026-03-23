package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.PaymentCCListRequest;
import ai.anamaya.service.oms.core.dto.response.PaymentCCResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final Map<String, PaymentProvider> paymentProviders;

    private PaymentProvider getProvider(String source) {
        String key = (source != null ? source.toLowerCase() : "biztrip") + "PaymentProvider";
        PaymentProvider provider = paymentProviders.get(key);

        if (provider == null) {
            log.warn("Provider '{}' not found, fallback to 'biztripPaymentProvider'", key);
            provider = paymentProviders.get("biztripPaymentProvider");
        }

        return provider;
    }

    public List<PaymentCCResponse> getCC(CallerContext callerContext, String source, PaymentCCListRequest request) {
        return getProvider(source).getCC(callerContext, request);
    }

}
