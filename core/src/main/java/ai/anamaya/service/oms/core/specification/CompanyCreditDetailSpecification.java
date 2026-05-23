package ai.anamaya.service.oms.core.specification;

import ai.anamaya.service.oms.core.dto.request.CreditMonitoringFilter;
import ai.anamaya.service.oms.core.entity.CompanyCreditDetail;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CompanyCreditDetailSpecification {

    public static Specification<CompanyCreditDetail> filter(CreditMonitoringFilter f, Long balanceId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (balanceId != null) {
                predicates.add(cb.equal(root.get("balance").get("id"), balanceId));
            } else if (f.getCompanyId() != null && f.getCompanyId() != 0) {
                predicates.add(cb.equal(root.get("balance").get("companyId"), f.getCompanyId()));
            }

            if (f.getSourceType() != null) {
                predicates.add(cb.equal(root.get("sourceType"), f.getSourceType()));
            }

            if (f.getBookingType() != null) {
                predicates.add(cb.equal(root.get("bookingType"), f.getBookingType()));
            }

            if (f.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                    root.get("createdAt"),
                    f.getStartDate().atStartOfDay()
                ));
            }

            if (f.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                    root.get("createdAt"),
                    f.getEndDate().atTime(23, 59, 59)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
