package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.BalanceMonitoringFilter;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.BalanceMonitoringResponse;
import ai.anamaya.service.oms.core.dto.response.BalanceRecapDailyResponse;
import ai.anamaya.service.oms.core.enums.BalanceCodeType;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.BalanceMonitoringExportService;
import ai.anamaya.service.oms.core.service.BalanceMonitoringService;
import ai.anamaya.service.oms.core.service.BalanceRecapDailyService;
import ai.anamaya.service.oms.core.service.BalanceService;
import ai.anamaya.service.oms.core.util.SecurityUtil;
import ai.anamaya.service.oms.rest.dto.request.BalanceAdjustRequestRest;
import ai.anamaya.service.oms.rest.dto.request.BalanceRecapDailyRequestRest;
import ai.anamaya.service.oms.rest.dto.request.BalanceTopUpRequestRest;
import ai.anamaya.service.oms.rest.dto.response.BalanceMonitoringResponseRest;
import ai.anamaya.service.oms.rest.dto.response.BalanceRecapDailyResponseRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyBalanceResponseRest;
import ai.anamaya.service.oms.rest.mapper.BalanceMapper;
import ai.anamaya.service.oms.rest.mapper.BalanceRecapDailyMapper;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;
    private final BalanceRecapDailyService balanceRecapDailyService;
    private final BalanceMonitoringService balanceMonitoringService;
    private final BalanceMonitoringExportService balanceMonitoringExportService;
    private final BalanceMapper mapper;
    private final BalanceRecapDailyMapper recapMapper;
    private final JwtUtils jwtUtils;

    @PostMapping("/adjust")
    public ApiResponse<CompanyBalanceResponseRest> adjust(
        @Valid @RequestBody BalanceAdjustRequestRest reqRest) {

        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        var reqCore = mapper.toCore(reqRest);
        var result = balanceService.adjustBalance(userCallerContext, reqCore);

        return ApiResponse.success(mapper.toRest(result));
    }

    @PostMapping("/topup")
    public ApiResponse<CompanyBalanceResponseRest> topUp(
        @Valid @RequestBody BalanceTopUpRequestRest reqRest) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        var reqCore = mapper.toCore(reqRest);
        var result = balanceService.topUpBalance(userCallerContext, reqCore);

        return ApiResponse.success(mapper.toRest(result));
    }

    @GetMapping
    public ApiResponse<List<CompanyBalanceResponseRest>> getAll() {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        var list = balanceService.getBalancesByCompany(userCallerContext)
            .stream()
            .map(mapper::toRest)
            .toList();

        return ApiResponse.success(list);
    }

    @PreAuthorize("hasRole('SYSTEM')")
    @GetMapping("/external")
    public ApiResponse<List<CompanyBalanceResponseRest>> getAllExternal(
        @RequestParam String accountId
    ) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("accountId is required");
        }

        var list = balanceService.getBalancesByBiztripAccountId(accountId)
            .stream()
            .map(mapper::toRest)
            .toList();

        return ApiResponse.success(list);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @PostMapping("/recap/daily")
    public ApiResponse<List<BalanceRecapDailyResponseRest>> recapDaily(
        @Valid @RequestBody BalanceRecapDailyRequestRest reqRest) {

        LocalDate date = LocalDate.parse(reqRest.getDate());
        List<BalanceRecapDailyResponse> results = balanceRecapDailyService.recapDailyBalance(date);
        return ApiResponse.success(results.stream().map(recapMapper::toRest).toList());
    }

    @GetMapping("/monitoring")
    public ApiResponse<List<BalanceMonitoringResponseRest>> monitoring(
        @ModelAttribute BalanceMonitoringFilter filter,
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

        Page<BalanceMonitoringResponse> result = balanceMonitoringService.getMonitoring(filter, pageable);

        List<BalanceMonitoringResponseRest> data = result.getContent().stream()
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
        @ModelAttribute BalanceMonitoringFilter filter) {

        Long jwtCompanyId = jwtUtils.getCompanyIdFromToken();
        boolean isSuperAdmin = SecurityUtil.hasRole("SUPER_ADMIN");

        if (!isSuperAdmin) {
            filter.setCompanyId(jwtCompanyId);
        } else if (filter.getCompanyId() == null || filter.getCompanyId() == 0) {
            filter.setCompanyId(jwtCompanyId);
        }

        byte[] csv = balanceMonitoringExportService.exportToCsv(filter);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("text/csv"))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=balance-monitoring.csv")
            .body(csv);
    }

    @GetMapping("/{code}")
    public ApiResponse<Map<String, Object>> getBalanceDetails(
        @PathVariable BalanceCodeType code,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        return balanceService.getBalanceDetails(userCallerContext, code, page, size);
    }
}
