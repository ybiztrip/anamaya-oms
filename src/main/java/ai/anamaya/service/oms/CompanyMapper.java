package ai.anamaya.service.oms;

import ai.anamaya.service.oms.dto.CompanyRequest;
import ai.anamaya.service.oms.dto.CompanyResponse;
import ai.anamaya.service.oms.dto.*;
import ai.anamaya.service.oms.model.Company;

public class CompanyMapper {

    public static Company toEntity(CompanyRequest request) {
        return Company.builder()
                .name(request.getName())
                .status(request.getStatus())
                .createdBy(request.getCreatedBy())
                .build();
    }

    public static CompanyResponse toResponse(Company company) {
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
