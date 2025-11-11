package ai.anamaya.service.oms.repository;

import ai.anamaya.service.oms.entity.CompanyBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyBalanceRepository extends JpaRepository<CompanyBalance, Long> {

    List<CompanyBalance> findByCompanyId(Long companyId);

    Optional<CompanyBalance> findByCompanyIdAndCode(Long companyId, String code);
}
