package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.*;
import ai.anamaya.service.oms.core.dto.response.TravelPolicyResponse;
import ai.anamaya.service.oms.core.entity.*;
import ai.anamaya.service.oms.core.enums.ActivityLogType;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.*;
import ai.anamaya.service.oms.core.specification.TravelPolicySpecification;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Call;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TravelPolicyService {


    private final ActivityLogRepository activityLogRepository;
    private final TravelPolicyRepository repository;
    private final ObjectMapper objectMapper;

    public Page<TravelPolicyResponse> getAll(CallerContext callerContext, TravelPolicyListFilter filter) {

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

        Specification<TravelPolicy> spec = TravelPolicySpecification.filter(filter);

        Page<TravelPolicy> travelPolicies = repository.findAll(spec, pageable);

        List<TravelPolicyResponse> mapped = travelPolicies.getContent().stream()
            .map(this::toResponse)
            .toList();

        return new PageImpl<>(mapped, pageable, travelPolicies.getTotalElements());
    }

    public TravelPolicy getById(CallerContext callerContext, Long id) {
        long companyId = callerContext.companyId();

        Optional<TravelPolicy> data = repository.findById(id);
        if (data.isEmpty()) {
            throw new NotFoundException("Travel policy not found");
        }

        TravelPolicy travelPolicy = data.get();
        if(!Objects.equals(travelPolicy.getCompanyId(), companyId)) {
            throw new IllegalArgumentException("Invalid data");
        }

        return travelPolicy;
    }

    @Transactional
    public TravelPolicyResponse create(CallerContext callerContext, TravelPolicyRequest request) {
        Long userId = callerContext.userId();

        if (repository.existsByNameAndCompanyId(request.getName(), request.getCompanyId())) {
            throw new IllegalArgumentException("Name already exists");
        }

        TravelPolicy travelPolicy = TravelPolicy.builder()
            .companyId(request.getCompanyId())
            .name(request.getName())
            .flights(request.getFlights())
            .flightMinimumPrice(request.getFlightMinimumPrice())
            .flightMaximumPrice(request.getFlightMaximumPrice())
            .flightMinimumClass(request.getFlightMinimumClass())
            .flightMaximumClass(request.getFlightMaximumClass())
            .hotelMinimumPrice(request.getHotelMinimumPrice())
            .hotelMaximumPrice(request.getHotelMaximumPrice())
            .hotelMinimumClass(request.getHotelMinimumClass())
            .hotelMaximumClass(request.getHotelMaximumClass())
            .hotelPagu(request.getHotelPagu())
            .createdBy(userId)
            .updatedBy(userId)
            .status((short) 1)
            .build();

        repository.save(travelPolicy);

        JsonNode jsonData = objectMapper.valueToTree(travelPolicy);
        ActivityLog activityLog = ActivityLog.builder()
            .companyId(travelPolicy.getCompanyId())
            .type(ActivityLogType.TRAVEL_POLICY)
            .referenceId(travelPolicy.getId())
            .data(jsonData)
            .createdBy(userId)
            .updatedBy(userId)
            .status((short) 1)
            .build();

        activityLogRepository.save(activityLog);

        return toResponse(travelPolicy);
    }

    @Transactional
    public TravelPolicyResponse update(CallerContext callerContext, Long id, TravelPolicyRequest request) {
        Long userId = callerContext.userId();

        TravelPolicy travelPolicy = getById(callerContext, id);

        if (!Objects.equals(travelPolicy.getName(), request.getName())) {
            if (repository.existsByNameAndCompanyId(request.getName(), request.getCompanyId())) {
                throw new IllegalArgumentException("Name already exists");
            }
        }

        travelPolicy.setName(request.getName());
        travelPolicy.setFlights(request.getFlights());
        travelPolicy.setFlightMinimumPrice(request.getFlightMinimumPrice());
        travelPolicy.setFlightMaximumPrice(request.getFlightMaximumPrice());
        travelPolicy.setFlightMinimumClass(request.getFlightMinimumClass());
        travelPolicy.setFlightMaximumClass(request.getFlightMaximumClass());
        travelPolicy.setHotelMinimumPrice(request.getHotelMinimumPrice());
        travelPolicy.setHotelMaximumPrice(request.getHotelMaximumPrice());
        travelPolicy.setHotelMinimumClass(request.getHotelMinimumClass());
        travelPolicy.setHotelMaximumClass(request.getHotelMaximumClass());
        travelPolicy.setHotelPagu(request.getHotelPagu());
        travelPolicy.setUpdatedBy(userId);
        travelPolicy.setStatus(request.getStatus());
        repository.save(travelPolicy);

        JsonNode jsonData = objectMapper.valueToTree(travelPolicy);
        ActivityLog activityLog = ActivityLog.builder()
            .companyId(travelPolicy.getCompanyId())
            .type(ActivityLogType.TRAVEL_POLICY)
            .referenceId(travelPolicy.getId())
            .data(jsonData)
            .createdBy(userId)
            .updatedBy(userId)
            .status((short) 1)
            .build();

        activityLogRepository.save(activityLog);

        return toResponse(travelPolicy);
    }

    private TravelPolicyResponse toResponse(TravelPolicy t) {
        return TravelPolicyResponse.builder()
            .id(t.getId())
            .companyId(t.getCompanyId())
            .name(t.getName())
            .flights(t.getFlights())
            .flightMinimumPrice(t.getFlightMinimumPrice())
            .flightMaximumPrice(t.getFlightMaximumPrice())
            .flightMinimumClass(t.getFlightMinimumClass())
            .flightMaximumClass(t.getFlightMaximumClass())
            .hotelMinimumPrice(t.getHotelMinimumPrice())
            .hotelMaximumPrice(t.getHotelMaximumPrice())
            .hotelMinimumClass(t.getHotelMinimumClass())
            .hotelMaximumClass(t.getHotelMaximumClass())
            .hotelPagu(t.getHotelPagu())
            .status(t.getStatus())
            .createdBy(t.getCreatedBy())
            .createdAt(t.getCreatedAt() != null ? t.getCreatedAt().toString() : null)
            .updatedBy(t.getUpdatedBy())
            .updatedAt(t.getUpdatedAt() != null ? t.getUpdatedAt().toString() : null)
            .build();
    }

}
