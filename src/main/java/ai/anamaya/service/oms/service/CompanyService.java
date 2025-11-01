package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.ApiResponse;
import ai.anamaya.service.oms.CompanyMapper;
import ai.anamaya.service.oms.dto.CompanyRequest;
import ai.anamaya.service.oms.dto.CompanyResponse;
import ai.anamaya.service.oms.model.Company;
import ai.anamaya.service.oms.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CompanyService {

    private final CompanyRepository repository;

    public CompanyService(CompanyRepository repository) {
        this.repository = repository;
    }

    public ApiResponse<List<CompanyResponse>> findAll() {
        var list = repository.findAll().stream()
                .map(CompanyMapper::toResponse)
                .toList();
        return ApiResponse.success(list);
    }

    public ApiResponse<CompanyResponse> findById(Long id) {
        return repository.findById(id)
                .map(company -> ApiResponse.success(CompanyMapper.toResponse(company)))
                .orElseGet(() -> ApiResponse.error("Company not found"));
    }

    public ApiResponse<CompanyResponse> create(CompanyRequest request) {
        Company company = CompanyMapper.toEntity(request);
        repository.save(company);
        return ApiResponse.success(CompanyMapper.toResponse(company));
    }

    public ApiResponse<CompanyResponse> update(Long id, CompanyRequest request) {
        var company = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        company.setName(request.getName());
        company.setStatus(request.getStatus());
        company.setUpdatedBy(request.getCreatedBy());
        repository.save(company);

        return ApiResponse.success(CompanyMapper.toResponse(company));
    }

    public ApiResponse<String> delete(Long id) {
        if (!repository.existsById(id)) {
            return ApiResponse.error("Company not found");
        }
        repository.deleteById(id);
        return ApiResponse.success("Company deleted successfully");
    }
}
