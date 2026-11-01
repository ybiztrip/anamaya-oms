package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.RefundService;
import ai.anamaya.service.oms.rest.dto.request.RefundAdminCreateRequestRest;
import ai.anamaya.service.oms.rest.dto.response.RefundResponseRest;
import ai.anamaya.service.oms.rest.mapper.RefundMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/refunds")
@RequiredArgsConstructor
public class RefundAdminController {

    private final RefundService refundService;
    private final RefundMapper mapper;
    private final JwtUtils jwtUtils;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @PostMapping
    public ApiResponse<RefundResponseRest> create(@Valid @RequestBody RefundAdminCreateRequestRest reqRest) {
        UserCallerContext ctx = new UserCallerContext(
            jwtUtils.getCompanyIdFromToken(),
            jwtUtils.getUserIdFromToken(),
            jwtUtils.getEmailFromToken());
        var result = refundService.createAdminRefund(ctx, mapper.toCore(reqRest));
        return ApiResponse.success(mapper.toRest(result));
    }
}
