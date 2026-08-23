package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.internal.ProviderService;
import ai.anamaya.service.oms.core.client.internal.dto.request.ProviderFetchRequest;
import ai.anamaya.service.oms.core.client.internal.dto.request.ProviderSaveRequest;
import ai.anamaya.service.oms.core.client.internal.dto.response.ProviderResponse;
import ai.anamaya.service.oms.core.context.CallerContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderAdminService {

    private final ProviderService providerService;

    public List<ProviderResponse> fetchProvider(CallerContext callerContext, ProviderFetchRequest request) {
        return providerService.fetchProvider(callerContext, request);
    }

    public String saveProvider(CallerContext callerContext, ProviderSaveRequest request) {
        return providerService.saveProvider(callerContext, request);
    }
}
