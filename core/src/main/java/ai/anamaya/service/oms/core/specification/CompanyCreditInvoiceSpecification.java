package ai.anamaya.service.oms.core.specification;

import ai.anamaya.service.oms.core.dto.request.CompanyCreditInvoiceListFilter;
import ai.anamaya.service.oms.core.entity.CompanyCreditInvoice;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CompanyCreditInvoiceSpecification {

    public static Specification<CompanyCreditInvoice> filter(CompanyCreditInvoiceListFilter filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getCompanyId() != null && filter.getCompanyId() != 0) {
                predicates.add(cb.equal(root.get("companyId"), filter.getCompanyId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
