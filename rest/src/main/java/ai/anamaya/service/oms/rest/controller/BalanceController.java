package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.enums.BalanceCodeType;
import ai.anamaya.service.oms.core.service.BalanceService;
import ai.anamaya.service.oms.rest.dto.request.BalanceAdjustRequestRest;
import ai.anamaya.service.oms.rest.dto.request.BalanceTopUpRequestRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyBalanceResponseRest;
import ai.anamaya.service.oms.rest.mapper.BalanceMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;
    private final BalanceMapper mapper;

    @PostMapping("/adjust")
    public ApiResponse<CompanyBalanceResponseRest> adjust(
        @Valid @RequestBody BalanceAdjustRequestRest reqRest) {

        var reqCore = mapper.toCore(reqRest);
        var result = balanceService.adjustBalance(reqCore);

        return ApiResponse.success(mapper.toRest(result));
    }

    @PostMapping("/topup")
    public ApiResponse<CompanyBalanceResponseRest> topUp(
        @Valid @RequestBody BalanceTopUpRequestRest reqRest) {

        var reqCore = mapper.toCore(reqRest);
        var result = balanceService.topUpBalance(reqCore);

        return ApiResponse.success(mapper.toRest(result));
    }

    @GetMapping
    public ApiResponse<List<CompanyBalanceResponseRest>> getAll() {
        var list = balanceService.getBalancesByCompany()
            .stream()
            .map(mapper::toRest)
            .toList();

        return ApiResponse.success(list);
    }

    @GetMapping("/{code}")
    public ApiResponse<Map<String, Object>> getBalanceDetails(
        @PathVariable BalanceCodeType code,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        return balanceService.getBalanceDetails(code, page, size);
    }
}
