package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.CompanyCredit;
import ai.anamaya.service.oms.core.enums.CreditCodeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyCreditRepository extends JpaRepository<CompanyCredit, Long> {

    List<CompanyCredit> findByCompanyId(Long companyId);

    Optional<CompanyCredit> findByCompanyIdAndCode(Long companyId, CreditCodeType code);
}
