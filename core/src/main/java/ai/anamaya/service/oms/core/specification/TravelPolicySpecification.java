package ai.anamaya.service.oms.core.specification;

import ai.anamaya.service.oms.core.dto.request.TravelPolicyListFilter;
import ai.anamaya.service.oms.core.entity.TravelPolicy;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TravelPolicySpecification {

    public static Specification<TravelPolicy> filter(TravelPolicyListFilter filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getCompanyId() != null && filter.getCompanyId() != 0) {
                predicates.add(cb.equal(root.get("companyId"), filter.getCompanyId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
