package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.RefundService;
import ai.anamaya.service.oms.rest.dto.request.RefundCreateRequestRest;
import ai.anamaya.service.oms.rest.dto.request.RefundPaidRequestRest;
import ai.anamaya.service.oms.rest.dto.response.RefundResponseRest;
import ai.anamaya.service.oms.rest.mapper.RefundMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;
    private final RefundMapper mapper;
    private final JwtUtils jwtUtils;

    @PostMapping
    public ApiResponse<RefundResponseRest> create(@Valid @RequestBody RefundCreateRequestRest reqRest) {
        UserCallerContext ctx = callerContext();
        var result = refundService.createRefund(ctx, mapper.toCore(reqRest));
        return ApiResponse.success(mapper.toRest(result));
    }

    @PreAuthorize("hasAnyRole('OFFICELESS', 'SYSTEM')")
    @PostMapping("/{id}/paid")
    public ApiResponse<RefundResponseRest> paid(
        @PathVariable Long id,
        @Valid @RequestBody RefundPaidRequestRest reqRest
    ) {
        UserCallerContext ctx = callerContext();
        var result = refundService.paidRefund(ctx, id, mapper.toCore(reqRest));
        return ApiResponse.success(mapper.toRest(result));
    }

    @PreAuthorize("hasAnyRole('OFFICELESS', 'SYSTEM')")
    @PostMapping("/{id}/cancel")
    public ApiResponse<RefundResponseRest> cancel(@PathVariable Long id) {
        UserCallerContext ctx = callerContext();
        var result = refundService.cancelRefund(ctx, id);
        return ApiResponse.success(mapper.toRest(result));
    }

    private UserCallerContext callerContext() {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        return new UserCallerContext(companyId, userId, userEmail);
    }
}
