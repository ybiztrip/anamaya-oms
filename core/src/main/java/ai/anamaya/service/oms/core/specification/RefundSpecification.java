package ai.anamaya.service.oms.core.specification;

import ai.anamaya.service.oms.core.dto.request.RefundFilter;
import ai.anamaya.service.oms.core.entity.Refund;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RefundSpecification {

    public static Specification<Refund> filter(RefundFilter f) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (f.getCompanyId() != null) {
                predicates.add(cb.equal(root.get("companyId"), f.getCompanyId()));
            }

            if (f.getBookingType() != null) {
                predicates.add(cb.equal(root.get("bookingType"), f.getBookingType()));
            }

            if (f.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), f.getStatus()));
            }

            if (f.getPaymentMethod() != null) {
                predicates.add(cb.equal(root.get("paymentMethod"), f.getPaymentMethod()));
            }

            if (f.getCode() != null && !f.getCode().isBlank()) {
                predicates.add(cb.equal(root.get("code"), f.getCode()));
            }

            if (f.getBookingCode() != null && !f.getBookingCode().isBlank()) {
                predicates.add(cb.equal(root.get("bookingCode"), f.getBookingCode()));
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
