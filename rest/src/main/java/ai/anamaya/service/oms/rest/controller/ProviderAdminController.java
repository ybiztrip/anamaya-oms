package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.client.internal.dto.request.ProviderFetchRequest;
import ai.anamaya.service.oms.core.client.internal.dto.request.ProviderSaveRequest;
import ai.anamaya.service.oms.core.client.internal.dto.response.ProviderResponse;
import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.ProviderAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/provider")
public class ProviderAdminController {

    private final ProviderAdminService providerAdminService;
    private final JwtUtils jwtUtils;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @PostMapping("/fetch")
    public ApiResponse<List<ProviderResponse>> fetchProvider(@Valid @RequestBody ProviderFetchRequest request) {
        UserCallerContext callerContext = buildCallerContext();
        List<ProviderResponse> response = providerAdminService.fetchProvider(callerContext, request);
        return ApiResponse.success(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @PostMapping("/save")
    public ApiResponse<String> saveProvider(@Valid @RequestBody ProviderSaveRequest request) {
        UserCallerContext callerContext = buildCallerContext();
        String response = providerAdminService.saveProvider(callerContext, request);
        return ApiResponse.success(response);
    }

    private UserCallerContext buildCallerContext() {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        return new UserCallerContext(companyId, userId, userEmail);
    }
}
