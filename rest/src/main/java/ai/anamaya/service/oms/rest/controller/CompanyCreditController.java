package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.CompanyCreditInvoiceListFilter;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.CompanyCreditService;
import ai.anamaya.service.oms.rest.dto.request.CompanyCreditInvoiceRequestRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyCreditInvoiceResponseRest;
import ai.anamaya.service.oms.rest.mapper.CompanyCreditMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company-credit")
@RequiredArgsConstructor
public class CompanyCreditController {

    private final CompanyCreditService service;
    private final CompanyCreditMapper mapper;
    private final JwtUtils jwtUtils;

    @GetMapping("/invoices")
    public ApiResponse<List<CompanyCreditInvoiceResponseRest>> getAll(
        @ModelAttribute CompanyCreditInvoiceListFilter filter
    ) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        filter.setCompanyId(companyId);
        var pageResult = service.getAll(userCallerContext, filter);

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

}
