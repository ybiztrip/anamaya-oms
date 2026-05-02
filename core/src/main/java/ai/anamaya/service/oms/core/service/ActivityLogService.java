package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.ActivityLogListFilter;
import ai.anamaya.service.oms.core.dto.response.ActivityLogResponse;
import ai.anamaya.service.oms.core.entity.ActivityLog;
import ai.anamaya.service.oms.core.repository.ActivityLogRepository;
import ai.anamaya.service.oms.core.specification.ActivityLogSpecification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository repository;
    private final ObjectMapper objectMapper;

    public Page<ActivityLogResponse> getAll(CallerContext callerContext, ActivityLogListFilter filter) {

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

        Specification<ActivityLog> spec = ActivityLogSpecification.filter(filter);

        Page<ActivityLog> activityLogs = repository.findAll(spec, pageable);

        List<ActivityLogResponse> mapped = activityLogs.getContent().stream()
            .map(this::toResponse)
            .toList();

        return new PageImpl<>(mapped, pageable, activityLogs.getTotalElements());
    }

    private ActivityLogResponse toResponse(ActivityLog a) {
        return ActivityLogResponse.builder()
            .id(a.getId())
            .companyId(a.getCompanyId())
            .type(a.getType())
            .referenceId(a.getReferenceId())
            .status(a.getStatus())
            .createdBy(a.getCreatedBy())
            .createdAt(a.getCreatedAt() != null ? a.getCreatedAt().toString() : null)
            .updatedBy(a.getUpdatedBy())
            .updatedAt(a.getUpdatedAt() != null ? a.getUpdatedAt().toString() : null)
            .build();
    }

}
