package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.CompanyCreditInvoiceListFilter;
import ai.anamaya.service.oms.core.dto.request.CompanyCreditInvoiceRequest;
import ai.anamaya.service.oms.core.dto.request.CreditAdjustRequest;
import ai.anamaya.service.oms.core.dto.response.CompanyCreditInvoiceResponse;
import ai.anamaya.service.oms.core.entity.CompanyCreditInvoice;
import ai.anamaya.service.oms.core.enums.CreditSourceType;
import ai.anamaya.service.oms.core.enums.CreditTransactionType;
import ai.anamaya.service.oms.core.enums.InvoiceStatus;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.CompanyCreditInvoiceRepository;
import ai.anamaya.service.oms.core.specification.CompanyCreditInvoiceSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyCreditService {

    private  final CreditService creditService;
    private final CompanyCreditInvoiceRepository companyCreditInvoiceRepository;

    public Page<CompanyCreditInvoiceResponse> getAll(CallerContext callerContext, CompanyCreditInvoiceListFilter filter) {

        // Sorting
        Sort sorting = Sort.by("createdAt").descending();

        if (filter.getSort() != null && !filter.getSort().isBlank()) {
            String[] parts = filter.getSort().split(";");
            String field = parts[0];

            Sort.Direction direction =
                (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            sorting = Sort.by(direction, field);
        }

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sorting);

        Specification<CompanyCreditInvoice> spec = CompanyCreditInvoiceSpecification.filter(filter);

        Page<CompanyCreditInvoice> companyCreditInvoices = companyCreditInvoiceRepository.findAll(spec, pageable);

        List<CompanyCreditInvoiceResponse> mapped = companyCreditInvoices.getContent().stream()
            .map(this::toResponse)
            .toList();

        return new PageImpl<>(mapped, pageable, companyCreditInvoices.getTotalElements());
    }

    @Transactional
    public CompanyCreditInvoiceResponse createInvoice(CallerContext callerContext, CompanyCreditInvoiceRequest request) {
        Long companyId = callerContext.companyId();
        Long userId = callerContext.userId();

        if(companyCreditInvoiceRepository.existsByCompanyIdAndDocNo(companyId, request.getDocNo())) {
            throw new IllegalArgumentException("Doc no already exists");
        }

        CompanyCreditInvoice companyCreditInvoice = CompanyCreditInvoice
            .builder()
            .companyId(companyId)
            .code(request.getCode())
            .docNo(request.getDocNo())
            .amount(request.getAmount())
            .currency("IDR")
            .status(InvoiceStatus.CREATED)
            .createdBy(userId)
            .updatedBy(userId)
            .build();
        companyCreditInvoiceRepository.save(companyCreditInvoice);
        return toResponse(companyCreditInvoice);
    }

    @Transactional
    public CompanyCreditInvoiceResponse paidInvoice(CallerContext callerContext, Long id) {
        Optional<CompanyCreditInvoice> data = companyCreditInvoiceRepository.findById(id);
        if(data.isEmpty()) {
            throw new NotFoundException("Data not found");
        }

        CompanyCreditInvoice companyCreditInvoice = data.get();
        if(companyCreditInvoice.getStatus() == InvoiceStatus.PAID) {
            return toResponse(companyCreditInvoice);
        }

        creditService.adjustBalance(
            callerContext,
            CreditAdjustRequest.builder()
                .companyId(companyCreditInvoice.getCompanyId())
                .code(companyCreditInvoice.getCode())
                .sourceType(CreditSourceType.INVOICE)
                .type(CreditTransactionType.CREDIT)
                .amount(companyCreditInvoice.getAmount())
                .referenceId(companyCreditInvoice.getId())
                .referenceCode(companyCreditInvoice.getDocNo())
                .remarks("Invoice paid")
                .build());

        companyCreditInvoice.setStatus(InvoiceStatus.PAID);
        companyCreditInvoiceRepository.save(companyCreditInvoice);

        return toResponse(companyCreditInvoice);
    }

    private CompanyCreditInvoiceResponse toResponse(CompanyCreditInvoice c) {
        return CompanyCreditInvoiceResponse.builder()
            .id(c.getId())
            .companyId(c.getCompanyId())
            .code(c.getCode())
            .docNo(c.getDocNo())
            .amount(c.getAmount())
            .currency(c.getCurrency())
            .status(c.getStatus())
            .createdAt(c.getCreatedAt())
            .updatedAt(c.getUpdatedAt())
            .build();
    }
}
