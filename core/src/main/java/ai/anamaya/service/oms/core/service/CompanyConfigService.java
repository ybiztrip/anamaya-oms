package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.CompanyConfigUpdateRequest;
import ai.anamaya.service.oms.core.dto.response.CompanyConfigResponse;
import ai.anamaya.service.oms.core.entity.CompanyConfig;
import ai.anamaya.service.oms.core.repository.CompanyConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CompanyConfigService {

    private final CompanyConfigRepository repository;

    public CompanyConfigService(CompanyConfigRepository repository) {
        this.repository = repository;
    }

    public List<CompanyConfigResponse> list(CallerContext callerContext) {
        return repository.findAllByCompanyIdOrderByCodeAsc(callerContext.companyId()).stream()
            .map(this::toResponse)
            .toList();
    }

    public CompanyConfigResponse updateValues(CallerContext callerContext, String code, CompanyConfigUpdateRequest request) {
        if (request.getValueStr() == null && request.getValueInt() == null && request.getValueBool() == null) {
            throw new RuntimeException("At least one of valueStr, valueInt, or valueBool must be provided");
        }

        CompanyConfig entity = repository.findByCompanyIdAndCode(callerContext.companyId(), code)
            .orElseThrow(() -> new RuntimeException("Company config not found for code: " + code));

        if (request.getValueStr() != null) {
            entity.setValueStr(request.getValueStr());
        }
        if (request.getValueInt() != null) {
            entity.setValueInt(request.getValueInt());
        }
        if (request.getValueBool() != null) {
            entity.setValueBool(request.getValueBool());
        }

        repository.save(entity);
        return toResponse(entity);
    }

    private CompanyConfigResponse toResponse(CompanyConfig entity) {
        return CompanyConfigResponse.builder()
            .id(entity.getId())
            .companyId(entity.getCompanyId())
            .code(entity.getCode())
            .valueStr(entity.getValueStr())
            .valueInt(entity.getValueInt())
            .valueBool(entity.getValueBool())
            .status(entity.getStatus())
            .isVisible(entity.getIsVisible())
            .createdBy(entity.getCreatedBy())
            .createdAt(entity.getCreatedAt())
            .updatedBy(entity.getUpdatedBy())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
