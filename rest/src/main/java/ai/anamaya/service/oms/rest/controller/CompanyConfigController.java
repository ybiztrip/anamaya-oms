package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.CompanyConfigBatchUpdateRequest;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.CompanyConfigService;
import ai.anamaya.service.oms.rest.dto.request.CompanyConfigBatchUpdateRequestRest;
import ai.anamaya.service.oms.rest.dto.response.ApiResponse;
import ai.anamaya.service.oms.rest.dto.response.CompanyConfigResponseRest;
import ai.anamaya.service.oms.rest.mapper.CompanyConfigMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company-configs")
@RequiredArgsConstructor
public class CompanyConfigController {

    private final CompanyConfigService companyConfigService;
    private final CompanyConfigMapper companyConfigMapper;
    private final JwtUtils jwtUtils;

    @PreAuthorize("hasRole('COMPANY_ADMIN')")
    @GetMapping
    public ApiResponse<List<CompanyConfigResponseRest>> list() {
        var list = companyConfigService.list(callerContext());
        return ApiResponse.success(companyConfigMapper.toRestList(list));
    }

    @PreAuthorize("hasRole('COMPANY_ADMIN')")
    @PutMapping
    public ApiResponse<List<CompanyConfigResponseRest>> updateBatch(
        @Valid @RequestBody CompanyConfigBatchUpdateRequestRest requestRest
    ) {
        CompanyConfigBatchUpdateRequest requestCore = companyConfigMapper.toCore(requestRest);
        var updated = companyConfigService.updateBatch(callerContext(), requestCore);
        return ApiResponse.success(companyConfigMapper.toRestList(updated));
    }

    private UserCallerContext callerContext() {
        return new UserCallerContext(
            jwtUtils.getCompanyIdFromToken(),
            jwtUtils.getUserIdFromToken(),
            jwtUtils.getEmailFromToken()
        );
    }
}
