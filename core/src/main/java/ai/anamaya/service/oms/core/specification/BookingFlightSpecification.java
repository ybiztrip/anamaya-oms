package ai.anamaya.service.oms.core.specification;

import ai.anamaya.service.oms.core.dto.request.BookingFlightListFilter;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BookingFlightSpecification {

    public static Specification<BookingFlight> filter(BookingFlightListFilter filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
                predicates.add(root.get("status").in(filter.getStatuses()));
            }

            if (filter.getCompanyId() != null && filter.getCompanyId() != 0) {
                predicates.add(cb.equal(root.get("companyId"), filter.getCompanyId()));
            }

            if (filter.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                    root.get("createdAt"),
                    filter.getDateFrom().atStartOfDay()
                ));
            }

            if (filter.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                    root.get("createdAt"),
                    filter.getDateTo().atTime(23, 59, 59)
                ));
            }

            if (filter.getPaymentMethod() != null) {
                predicates.add(cb.equal(root.get("paymentMethod"), filter.getPaymentMethod()));
            }

            if (Boolean.TRUE.equals(filter.getInvoiceCandidate())) {
                predicates.add(cb.equal(root.get("paymentMethod"), BookingPaymentMethod.CREDIT));
                predicates.add(cb.isNull(root.get("invoiceId")));
                predicates.add(root.get("status").in(
                    BookingFlightStatus.PAID,
                    BookingFlightStatus.ISSUED
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
