package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.BalanceAdjustRequest;
import ai.anamaya.service.oms.dto.request.BalanceTopUpRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.CompanyBalanceDetailResponse;
import ai.anamaya.service.oms.dto.response.CompanyBalanceResponse;
import ai.anamaya.service.oms.entity.*;
import ai.anamaya.service.oms.enums.*;
import ai.anamaya.service.oms.exception.NotFoundException;
import ai.anamaya.service.oms.repository.*;
import ai.anamaya.service.oms.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final CompanyBalanceRepository balanceRepository;
    private final CompanyBalanceDetailRepository detailRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public CompanyBalanceResponse adjustBalance(BalanceAdjustRequest request) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();

        CompanyBalance balance = balanceRepository.findByCompanyIdAndCode(companyId, request.getCode())
                .orElseThrow(() -> new NotFoundException(
                        "Balance with code '" + request.getCode() + "' not found for your company."));

        return processBalanceAdjustment(
                balance,
                request.getType(),
                request.getAmount(),
                request.getSourceType(),
                request.getReferenceId(),
                request.getReferenceCode(),
                request.getRemarks(),
                userId
        );
    }

    @Transactional
    public CompanyBalanceResponse topUpBalance(BalanceTopUpRequest request) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();

        CompanyBalance balance = balanceRepository.findByCompanyIdAndCode(companyId, request.getCode())
                .orElseThrow(() -> new NotFoundException(
                        "Balance with code '" + request.getCode() + "' not found for your company."));

        // Type 1 = CREDIT
        return processBalanceAdjustment(
                balance,
                (short) 1,
                request.getAmount(),
                request.getSourceType(),
                request.getReferenceId(),
                request.getReferenceCode(),
                request.getRemarks() != null ? request.getRemarks() : "Balance top-up",
                userId
        );
    }

    private CompanyBalanceResponse processBalanceAdjustment(
            CompanyBalance balance,
            short typeValue,
            BigDecimal amount,
            short sourceType,
            Long referenceId,
            String referenceCode,
            String remarks,
            Long userId
    ) {
        BigDecimal begin = balance.getBalance();
        BigDecimal end;

        BalanceTransactionType type = BalanceTransactionType.fromValue(typeValue);

        switch (type) {
            case CREDIT -> {
                end = begin.add(amount);
            }
            case DEBIT -> {
                if (begin.compareTo(amount) < 0) {
                    throw new IllegalArgumentException("Insufficient balance for debit operation");
                }
                end = begin.subtract(amount);
            }
            default -> throw new IllegalArgumentException("Unsupported transaction type: " + type);
        }

        balance.setBalance(end);
        balance.setUpdatedBy(userId);
        balanceRepository.save(balance);

        CompanyBalanceDetail detail = CompanyBalanceDetail.builder()
                .balance(balance)
                .referenceId(referenceId)
                .referenceCode(referenceCode)
                .sourceType(sourceType)
                .type(type.getValue())
                .amount(amount)
                .beginBalance(begin)
                .endBalance(end)
                .remarks(remarks)
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        detailRepository.save(detail);

        return toResponse(balance);
    }

    public List<CompanyBalanceResponse> getBalancesByCompany() {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        return balanceRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> getBalanceDetails(String code, int page, int size) {
        Long companyId = jwtUtils.getCompanyIdFromToken();

        CompanyBalance balance = balanceRepository.findByCompanyIdAndCode(companyId, code)
                .orElseThrow(() -> new NotFoundException(
                        "Balance with code '" + code + "' not found for your company."));

        Pageable pageable = PageRequest.of(page, size);
        Page<CompanyBalanceDetail> detailsPage =
                detailRepository.findByBalanceIdOrderByCreatedAtDesc(balance.getId(), pageable);

        Map<String, Object> data = new HashMap<>();
        data.put("balance", toResponse(balance));
        data.put("transactions", Map.of(
                "details", detailsPage.getContent().stream()
                        .map(this::toDetailResponse)
                        .toList(),
                "page", detailsPage.getNumber(),
                "size", detailsPage.getSize(),
                "totalElements", detailsPage.getTotalElements(),
                "totalPages", detailsPage.getTotalPages()
        ));

        return ApiResponse.success(data);
    }

    private CompanyBalanceResponse toResponse(CompanyBalance balance) {
        return CompanyBalanceResponse.builder()
                .id(balance.getId())
                .companyId(balance.getCompanyId())
                .code(balance.getCode())
                .balance(balance.getBalance())
                .currency(balance.getCurrency())
                .status(balance.getStatus())
                .createdAt(balance.getCreatedAt())
                .updatedAt(balance.getUpdatedAt())
                .build();
    }

    private CompanyBalanceDetailResponse toDetailResponse(CompanyBalanceDetail detail) {
        return CompanyBalanceDetailResponse.builder()
                .id(detail.getId())
                .referenceId(detail.getReferenceId())
                .referenceCode(detail.getReferenceCode())
                .sourceType(detail.getSourceType())
                .type(detail.getType())
                .amount(detail.getAmount())
                .beginBalance(detail.getBeginBalance())
                .endBalance(detail.getEndBalance())
                .remarks(detail.getRemarks())
                .createdAt(detail.getCreatedAt())
                .build();
    }
}
