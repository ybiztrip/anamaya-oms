package ai.anamaya.service.oms.core.specification;

import ai.anamaya.service.oms.core.dto.request.UserGetListRequest;
import ai.anamaya.service.oms.core.entity.User;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filter(UserGetListRequest filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getCompanyId() != null && filter.getCompanyId() != 0) {
                predicates.add(
                    cb.equal(root.get("companyId"), filter.getCompanyId()
                    )
                );
            }

            if (filter.getUserId() != null) {
                predicates.add(
                    cb.equal(root.get("id"), filter.getUserId()
                    )
                );
            }

            if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
                predicates.add(
                    cb.like(
                        cb.lower(root.get("email")),
                        "%" + filter.getEmail().toLowerCase() + "%"
                    )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
