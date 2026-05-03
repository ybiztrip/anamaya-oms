package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.CompanyCreditInvoiceListFilter;
import ai.anamaya.service.oms.core.dto.request.CompanyCreditInvoiceRequest;
import ai.anamaya.service.oms.core.dto.request.TravelPolicyListFilter;
import ai.anamaya.service.oms.core.dto.response.CompanyCreditInvoiceResponse;
import ai.anamaya.service.oms.core.dto.response.TravelPolicyResponse;
import ai.anamaya.service.oms.core.entity.CompanyCreditInvoice;
import ai.anamaya.service.oms.core.entity.TravelPolicy;
import ai.anamaya.service.oms.core.enums.InvoiceStatus;
import ai.anamaya.service.oms.core.repository.CompanyCreditInvoiceRepository;
import ai.anamaya.service.oms.core.specification.CompanyCreditInvoiceSpecification;
import ai.anamaya.service.oms.core.specification.TravelPolicySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyCreditService {

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
