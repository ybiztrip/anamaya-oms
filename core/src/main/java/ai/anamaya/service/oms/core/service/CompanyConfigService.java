package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.CompanyConfigBatchUpdateItem;
import ai.anamaya.service.oms.core.dto.request.CompanyConfigBatchUpdateRequest;
import ai.anamaya.service.oms.core.dto.response.CompanyConfigResponse;
import ai.anamaya.service.oms.core.entity.CompanyConfig;
import ai.anamaya.service.oms.core.repository.CompanyConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Transactional
public class CompanyConfigService {

    /** Rows with this `is_visible` value are returned by {@link #list}. */
    private static final Short VISIBLE = 1;

    private final CompanyConfigRepository repository;

    public CompanyConfigService(CompanyConfigRepository repository) {
        this.repository = repository;
    }

    public List<CompanyConfigResponse> list(CallerContext callerContext) {
        return repository.findAllByCompanyIdAndIsVisibleOrderByCodeAsc(callerContext.companyId(), VISIBLE).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public List<CompanyConfigResponse> updateBatch(CallerContext callerContext, CompanyConfigBatchUpdateRequest request) {
        List<CompanyConfigBatchUpdateItem> items = request.getItems();
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("items must not be empty");
        }

        Set<String> seenCodes = new HashSet<>();
        for (CompanyConfigBatchUpdateItem item : items) {
            if (!seenCodes.add(item.getCode())) {
                throw new RuntimeException("Duplicate code in request: " + item.getCode());
            }
        }

        Long companyId = callerContext.companyId();
        List<CompanyConfigResponse> updated = new ArrayList<>(items.size());
        for (CompanyConfigBatchUpdateItem item : items) {
            if (item.getValueStr() == null && item.getValueInt() == null && item.getValueBool() == null) {
                throw new RuntimeException(
                    "At least one of valueStr, valueInt, or valueBool must be provided for code: " + item.getCode()
                );
            }

            CompanyConfig entity = repository.findByCompanyIdAndCode(companyId, item.getCode())
                .orElseThrow(() -> new RuntimeException("Company config not found for code: " + item.getCode()));

            if (!Objects.equals(entity.getIsVisible(), VISIBLE)) {
                throw new RuntimeException(
                    "Values can't be updated for this code: " + item.getCode()
                );
            }

            applyValueUpdates(entity, item);
            repository.save(entity);
            updated.add(toResponse(entity));
        }

        return updated;
    }

    private void applyValueUpdates(CompanyConfig entity, CompanyConfigBatchUpdateItem item) {
        if (item.getValueStr() != null) {
            entity.setValueStr(item.getValueStr());
        }
        if (item.getValueInt() != null) {
            entity.setValueInt(item.getValueInt());
        }
        if (item.getValueBool() != null) {
            entity.setValueBool(item.getValueBool());
        }
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
