package ai.anamaya.service.oms.core.specification;

import ai.anamaya.service.oms.core.dto.request.ActivityLogListFilter;
import ai.anamaya.service.oms.core.entity.ActivityLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ActivityLogSpecification {

    public static Specification<ActivityLog> filter(ActivityLogListFilter filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getReferenceId() != null && filter.getReferenceId() != 0) {
                predicates.add(cb.equal(root.get("referenceId"), filter.getReferenceId()));
            }

            if (filter.getCompanyId() != null && filter.getCompanyId() != 0) {
                predicates.add(cb.equal(root.get("companyId"), filter.getCompanyId()));
            }

            if (filter.getType() != null) {
                predicates.add(cb.equal(root.get("type"), filter.getType()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
