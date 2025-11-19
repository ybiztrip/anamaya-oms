package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.request.CompanyRequest;
import ai.anamaya.service.oms.core.dto.response.CompanyResponse;
import ai.anamaya.service.oms.core.entity.Company;
import ai.anamaya.service.oms.core.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
public class CompanyService {

    private final CompanyRepository repository;

    public CompanyService(CompanyRepository repository) {
        this.repository = repository;
    }

    public Page<CompanyResponse> findAll(int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);

        return repository.findAll(pageable)
            .map(this::toResponse);
    }

    public CompanyResponse findById(Long id) {
        Company company = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Company not found"));

        return toResponse(company);
    }

    public CompanyResponse create(CompanyRequest request) {
        Company company = Company.builder()
            .name(request.getName())
            .status(request.getStatus())
            .createdBy(request.getCreatedBy())
            .build();

        repository.save(company);
        return toResponse(company);
    }

    public CompanyResponse update(Long id, CompanyRequest request) {
        Company company = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Company not found"));

        company.setName(request.getName());
        company.setStatus(request.getStatus());
        company.setUpdatedBy(request.getCreatedBy());

        repository.save(company);
        return toResponse(company);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Company not found");
        }
        repository.deleteById(id);
    }

    private CompanyResponse toResponse(Company company) {
        return CompanyResponse.builder()
            .id(company.getId())
            .name(company.getName())
            .status(company.getStatus())
            .createdBy(company.getCreatedBy())
            .createdAt(company.getCreatedAt())
            .updatedBy(company.getUpdatedBy())
            .updatedAt(company.getUpdatedAt())
            .build();
    }
}
