package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.RefundFilter;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.RefundResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.RefundService;
import ai.anamaya.service.oms.rest.dto.request.RefundAdminCreateRequestRest;
import ai.anamaya.service.oms.rest.dto.response.RefundResponseRest;
import ai.anamaya.service.oms.rest.mapper.RefundMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @GetMapping
    public ApiResponse<List<RefundResponseRest>> list(
        @ModelAttribute RefundFilter filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort
    ) {
        Pageable pageable = PageRequest.of(
            page,
            size,
            sort != null ? Sort.by(sort) : Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<RefundResponse> result = refundService.getAdminList(filter, pageable);
        List<RefundResponseRest> data = result.getContent().stream()
            .map(mapper::toRest)
            .toList();
        return ApiResponse.paginatedSuccess(
            data,
            result.getTotalElements(),
            result.getTotalPages(),
            result.isLast(),
            result.getSize(),
            result.getNumber()
        );
    }
}
