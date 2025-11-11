package ai.anamaya.service.oms.controller;

import ai.anamaya.service.oms.dto.request.BalanceAdjustRequest;
import ai.anamaya.service.oms.dto.request.BalanceTopUpRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.CompanyBalanceResponse;
import ai.anamaya.service.oms.service.BalanceService;
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

    @PostMapping("/adjust")
    public ApiResponse<CompanyBalanceResponse> adjust(@RequestBody BalanceAdjustRequest request) {
        return ApiResponse.success(balanceService.adjustBalance(request));
    }

    @PostMapping("/topup")
    public ApiResponse<CompanyBalanceResponse> topUpBalance(@Valid @RequestBody BalanceTopUpRequest request) {
        return ApiResponse.success(balanceService.topUpBalance(request));
    }


    @GetMapping
    public ApiResponse<List<CompanyBalanceResponse>> getAll() {
        return ApiResponse.success(balanceService.getBalancesByCompany());
    }

    @GetMapping("/{code}")
    public ApiResponse<Map<String, Object>> getBalanceDetails(
            @PathVariable String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return balanceService.getBalanceDetails(code, page, size);
    }

}
