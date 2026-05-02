package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.TravelPolicyListFilter;
import ai.anamaya.service.oms.core.dto.request.TravelPolicyRequest;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.TravelPolicyService;
import ai.anamaya.service.oms.core.util.SecurityUtil;
import ai.anamaya.service.oms.rest.dto.request.TravelPolicyRequestRest;
import ai.anamaya.service.oms.rest.dto.response.ApiResponse;
import ai.anamaya.service.oms.rest.dto.response.TravelPolicyResponseRest;
import ai.anamaya.service.oms.rest.mapper.TravelPolicyMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/travel-policies")
@RequiredArgsConstructor
public class TravelPolicyController {

    private final JwtUtils jwtUtils;
    private final TravelPolicyMapper mapper;
    private final TravelPolicyService service;

    @GetMapping
    public ApiResponse<List<TravelPolicyResponseRest>> getAll(
        @ModelAttribute TravelPolicyListFilter filter
    ) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        filter.setCompanyId(companyId);
        var pageResult = service.getAll(userCallerContext, filter);

        List<TravelPolicyResponseRest> listRest = pageResult
            .getContent()
            .stream()
            .map(mapper::toRest)
            .toList();

        return ApiResponse.paginatedSuccess(
            listRest,
            pageResult.getTotalElements(),
            pageResult.getTotalPages(),
            pageResult.isLast(),
            pageResult.getSize(),
            pageResult.getNumber()
        );
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','COMPANY_ADMIN')")
    @PostMapping
    public ApiResponse<TravelPolicyResponseRest> create(@Valid @RequestBody TravelPolicyRequestRest reqRest) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        boolean isSuperAdmin = SecurityUtil.hasRole("SUPER_ADMIN");
        if (!isSuperAdmin) {
            reqRest.setCompanyId(companyId);
        }
        TravelPolicyRequest reqCore = mapper.toCore(reqRest);
        var result = service.create(userCallerContext, reqCore);

        return ApiResponse.success(mapper.toRest(result));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','COMPANY_ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<TravelPolicyResponseRest> update(
        @PathVariable Long id,
        @Valid @RequestBody TravelPolicyRequestRest reqRest) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        TravelPolicyRequest reqCore = mapper.toCore(reqRest);
        var result = service.update(userCallerContext, id, reqCore);

        return ApiResponse.success(mapper.toRest(result));
    }

}
