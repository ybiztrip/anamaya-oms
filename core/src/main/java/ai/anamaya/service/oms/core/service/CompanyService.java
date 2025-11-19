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

    public ApiResponse<List<CompanyResponse>> findAll(int page, int size, String sort) {
        Sort sorting = Sort.by("created_at").descending();

        if (sort != null && !sort.isBlank()) {
            String[] sortParams = sort.split(";");
            String sortField = sortParams[0];
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sorting = Sort.by(direction, sortField);
        }

        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<Company> companies = repository.findAll(pageable);

        List<CompanyResponse> data = companies.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.paginatedSuccess(
                data,
                companies.getTotalElements(),
                companies.getTotalPages(),
                companies.isLast(),
                companies.getSize(),
                companies.getNumber()
        );
    }

    public ApiResponse<CompanyResponse> findById(Long id) {
        return repository.findById(id)
                .map(company -> ApiResponse.success(toResponse(company)))
                .orElseGet(() -> ApiResponse.error("Company not found"));
    }

    public ApiResponse<CompanyResponse> create(CompanyRequest request) {
        Company company = Company.builder()
                .name(request.getName())
                .status(request.getStatus())
                .createdBy(request.getCreatedBy())
                .build();

        repository.save(company);
        return ApiResponse.success(toResponse(company));
    }

    public ApiResponse<CompanyResponse> update(Long id, CompanyRequest request) {
        var company = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        company.setName(request.getName());
        company.setStatus(request.getStatus());
        company.setUpdatedBy(request.getCreatedBy());
        repository.save(company);

        return ApiResponse.success(toResponse(company));
    }

    public ApiResponse<String> delete(Long id) {
        if (!repository.existsById(id)) {
            return ApiResponse.error("Company not found");
        }
        repository.deleteById(id);
        return ApiResponse.success("Company deleted successfully");
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
