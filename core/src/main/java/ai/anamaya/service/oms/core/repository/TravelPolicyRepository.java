package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.TravelPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelPolicyRepository extends JpaRepository<TravelPolicy, Long>, JpaSpecificationExecutor<TravelPolicy> {

    boolean existsByNameAndCompanyId(String email, Long companyId);

}
