package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.context.SystemCallerContext;
import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.CompanyCreditInvoiceListFilter;
import ai.anamaya.service.oms.core.dto.request.CreditMonitoringFilter;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.CreditMonitoringResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.CreditMonitoringExportService;
import ai.anamaya.service.oms.core.service.CreditMonitoringService;
import ai.anamaya.service.oms.core.service.CreditService;
import ai.anamaya.service.oms.core.util.SecurityUtil;
import ai.anamaya.service.oms.rest.dto.request.CompanyCreditInvoiceRequestRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyCreditInvoiceResponseRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyCreditResponseRest;
import ai.anamaya.service.oms.rest.dto.response.CreditMonitoringResponseRest;
import ai.anamaya.service.oms.rest.mapper.CompanyCreditMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company-credit")
@RequiredArgsConstructor
public class CompanyCreditController {

    private final CreditService service;
    private final CreditMonitoringService creditMonitoringService;
    private final CreditMonitoringExportService creditMonitoringExportService;
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

    @PreAuthorize("hasRole('SYSTEM')")
    @GetMapping("/external")
    public ApiResponse<List<CompanyCreditResponseRest>> getAllExternal(
        @RequestParam String accountId
    ) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("accountId is required");
        }

        var list = service.getBalancesByBiztripAccountId(accountId)
            .stream()
            .map(mapper::toRest)
            .toList();

        return ApiResponse.success(list);
    }

    @GetMapping("/monitoring")
    public ApiResponse<List<CreditMonitoringResponseRest>> monitoring(
        @ModelAttribute CreditMonitoringFilter filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort) {

        Long jwtCompanyId = jwtUtils.getCompanyIdFromToken();
        boolean isSuperAdmin = SecurityUtil.hasRole("SUPER_ADMIN");

        if (!isSuperAdmin) {
            filter.setCompanyId(jwtCompanyId);
        } else if (filter.getCompanyId() == null || filter.getCompanyId() == 0) {
            filter.setCompanyId(jwtCompanyId);
        }

        Pageable pageable = PageRequest.of(
            page,
            size,
            sort != null ? Sort.by(sort) : Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<CreditMonitoringResponse> result = creditMonitoringService.getMonitoring(filter, pageable);

        List<CreditMonitoringResponseRest> data = result.getContent().stream()
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

    @GetMapping("/monitoring/export")
    public ResponseEntity<byte[]> exportMonitoring(
        @ModelAttribute CreditMonitoringFilter filter) {

        Long jwtCompanyId = jwtUtils.getCompanyIdFromToken();
        boolean isSuperAdmin = SecurityUtil.hasRole("SUPER_ADMIN");

        if (!isSuperAdmin) {
            filter.setCompanyId(jwtCompanyId);
        } else if (filter.getCompanyId() == null || filter.getCompanyId() == 0) {
            filter.setCompanyId(jwtCompanyId);
        }

        byte[] csv = creditMonitoringExportService.exportToCsv(filter);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("text/csv"))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=credit-monitoring.csv")
            .body(csv);
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
