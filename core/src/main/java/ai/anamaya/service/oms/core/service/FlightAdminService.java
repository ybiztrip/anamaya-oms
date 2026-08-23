package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.internal.FlightPriceConfigService;
import ai.anamaya.service.oms.core.client.internal.dto.request.FlightPriceConfigSaveRequest;
import ai.anamaya.service.oms.core.client.internal.dto.response.FlightPriceConfigResponse;
import ai.anamaya.service.oms.core.context.CallerContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlightAdminService {

    private final FlightPriceConfigService flightPriceConfigService;

    public FlightPriceConfigResponse getPriceConfig(CallerContext callerContext, String accountId) {
        return flightPriceConfigService.getPriceConfig(callerContext, accountId);
    }

    public String savePriceConfig(CallerContext callerContext, FlightPriceConfigSaveRequest request) {
        return flightPriceConfigService.savePriceConfig(callerContext, request);
    }
}
