package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.BookingTravelPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingTravelPolicyRepository extends JpaRepository<BookingTravelPolicy, Long>, JpaSpecificationExecutor<BookingTravelPolicy> {

}
