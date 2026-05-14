package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.context.SystemCallerContext;
import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.CompanyCreditInvoiceListFilter;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.CreditService;
import ai.anamaya.service.oms.core.util.SecurityUtil;
import ai.anamaya.service.oms.rest.dto.request.CompanyCreditInvoiceRequestRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyCreditInvoiceResponseRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyCreditResponseRest;
import ai.anamaya.service.oms.rest.mapper.CompanyCreditMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company-credit")
@RequiredArgsConstructor
public class CompanyCreditController {

    private final CreditService service;
    private final CompanyCreditMapper mapper;
    private final JwtUtils jwtUtils;

    @GetMapping
    public ApiResponse<List<CompanyCreditResponseRest>> getAll(
        @RequestParam(required = false) Long companyId
    ) {
        CallerContext callerContext;
        if (SecurityUtil.hasRole("SYSTEM")) {
            if (companyId == null) {
                throw new IllegalArgumentException("companyId is required");
            }
            callerContext = new SystemCallerContext(companyId);
        } else {
            Long tokenCompanyId = jwtUtils.getCompanyIdFromToken();
            Long userId = jwtUtils.getUserIdFromToken();
            String userEmail = jwtUtils.getEmailFromToken();
            callerContext = new UserCallerContext(tokenCompanyId, userId, userEmail);
        }

        var list = service.getBalancesByCompany(callerContext)
            .stream()
            .map(mapper::toRest)
            .toList();

        return ApiResponse.success(list);
    }

    @GetMapping("/invoices")
    public ApiResponse<List<CompanyCreditInvoiceResponseRest>> getAll(
        @ModelAttribute CompanyCreditInvoiceListFilter filter
    ) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        filter.setCompanyId(companyId);
        var pageResult = service.getAllInvoice(userCallerContext, filter);

        List<CompanyCreditInvoiceResponseRest> listRest = pageResult
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

    @PreAuthorize("hasAnyRole('OFFICELESS')")
    @PostMapping("/invoices")
    public ApiResponse<CompanyCreditInvoiceResponseRest> createInvoice(
        @Valid @RequestBody CompanyCreditInvoiceRequestRest reqRest) {

        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        var reqCore = mapper.toCore(reqRest);
        var result = service.createInvoice(userCallerContext, reqCore);

        return ApiResponse.success(mapper.toRest(result));
    }

    @PreAuthorize("hasAnyRole('OFFICELESS')")
    @PostMapping("/invoices/{id}/paid")
    public ApiResponse<CompanyCreditInvoiceResponseRest> paidInvoice(
        @PathVariable Long id) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        var result = service.paidInvoice(userCallerContext, id);

        return ApiResponse.success(mapper.toRest(result));
    }

    @PreAuthorize("hasAnyRole('OFFICELESS')")
    @PostMapping("/invoices/{id}/cancel")
    public ApiResponse<CompanyCreditInvoiceResponseRest> cancelInvoice(
        @PathVariable Long id) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        var result = service.cancelInvoice(userCallerContext, id);

        return ApiResponse.success(mapper.toRest(result));
    }

}
