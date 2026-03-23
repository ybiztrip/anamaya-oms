package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.PaymentCCListRequest;
import ai.anamaya.service.oms.core.dto.response.*;

import java.util.List;

public interface PaymentProvider {
    List<PaymentCCResponse> getCC(CallerContext callerContext, PaymentCCListRequest request);
}
