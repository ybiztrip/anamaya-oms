package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.CreditAdjustRequest;
import ai.anamaya.service.oms.core.dto.request.CreditTopUpRequest;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.entity.CompanyCredit;
import ai.anamaya.service.oms.core.entity.CompanyCreditDetail;
import ai.anamaya.service.oms.core.enums.*;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.CompanyCreditDetailRepository;
import ai.anamaya.service.oms.core.repository.CompanyCreditRepository;
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
public class CreditService {

    private final CompanyCreditRepository creditRepository;
    private final CompanyCreditDetailRepository detailRepository;

    @Transactional
    public CompanyCreditResponse adjustBalance(CallerContext callerContext, CreditAdjustRequest request) {
        Long companyId = callerContext.companyId();
        Long userId = callerContext.userId();

        if(companyId == null && request.getCompanyId() != 0) {
            companyId = request.getCompanyId();
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        CompanyCredit balance = creditRepository.findByCompanyIdAndCode(companyId, request.getCode())
                .orElseThrow(() -> new NotFoundException(
                        "Credit with code '" + request.getCode() + "' not found for your company."));

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
    public CompanyCreditResponse topUpBalance(CallerContext callerContext, CreditTopUpRequest request) {
        Long companyId = callerContext.companyId();
        Long userId = callerContext.userId();

        CompanyCredit balance = creditRepository.findByCompanyIdAndCode(companyId, request.getCode())
                .orElseThrow(() -> new NotFoundException(
                        "Credit with code '" + request.getCode() + "' not found for your company."));

        return processBalanceAdjustment(
                balance,
                CreditTransactionType.CREDIT,
                request.getAmount(),
                CreditSourceType.TOPUP,
                request.getReferenceId(),
                request.getReferenceCode(),
                request.getRemarks() != null ? request.getRemarks() : "Credit top-up",
                userId
        );
    }

    private CompanyCreditResponse processBalanceAdjustment(
            CompanyCredit balance,
            CreditTransactionType type,
            BigDecimal amount,
            CreditSourceType sourceType,
            Long referenceId,
            String referenceCode,
            String remarks,
            Long userId
    ) {
        BigDecimal begin = balance.getBalance();
        BigDecimal end;

        switch (type) {
            case CREDIT -> end = begin.add(amount);
            case DEBIT -> {
                if (begin.compareTo(amount) < 0) {
                    throw new IllegalArgumentException("Insufficient Credit for debit operation");
                }
                end = begin.subtract(amount);
            }
            default -> throw new IllegalArgumentException("Unsupported transaction type: " + type);
        }

        balance.setBalance(end);
        balance.setUpdatedBy(userId);
        creditRepository.save(balance);

        CompanyCreditDetail detail = CompanyCreditDetail.builder()
                .balance(balance)
                .referenceId(referenceId)
                .referenceCode(referenceCode)
                .sourceType(sourceType)
                .type(type)
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

    public List<CompanyCreditResponse> getBalancesByCompany(CallerContext callerContext) {
        Long companyId = callerContext.companyId();
        return creditRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompanyCreditDetail> getBalanceDetailByReference(CreditSourceType sourceType, Long referenceId) {
        return detailRepository.findByReferenceIdAndSourceType(referenceId, sourceType);
    }
    @Transactional(readOnly = true)
    public List<CompanyCreditDetail> getBalanceDetailByReferenceCode(CreditSourceType sourceType, String referenceCode) {
        return detailRepository.findByReferenceCodeAndSourceType(referenceCode, sourceType);
    }

    @Transactional(readOnly = true)
    public ApiResponse<Map<String, Object>> getBalanceDetails(CallerContext callerContext, CreditCodeType code, int page, int size) {
        Long companyId = callerContext.companyId();

        CompanyCredit balance = creditRepository.findByCompanyIdAndCode(companyId, code)
                .orElseThrow(() -> new NotFoundException(
                        "Credit with code '" + code + "' not found for your company."));

        Pageable pageable = PageRequest.of(page, size);
        Page<CompanyCreditDetail> detailsPage =
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

    private CompanyCreditResponse toResponse(CompanyCredit balance) {
        return CompanyCreditResponse.builder()
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

    private CompanyCreditDetailResponse toDetailResponse(CompanyCreditDetail detail) {
        return CompanyCreditDetailResponse.builder()
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
