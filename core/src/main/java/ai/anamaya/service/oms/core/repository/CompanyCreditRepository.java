package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.CompanyCredit;
import ai.anamaya.service.oms.core.enums.CreditCodeType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyCreditRepository extends JpaRepository<CompanyCredit, Long> {

    List<CompanyCredit> findByCompanyId(Long companyId);

    Optional<CompanyCredit> findByCompanyIdAndCode(Long companyId, CreditCodeType code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CompanyCredit c WHERE c.companyId = :companyId AND c.code = :code")
    Optional<CompanyCredit> findByCompanyIdAndCodeForUpdate(
        @Param("companyId") Long companyId,
        @Param("code") CreditCodeType code
    );
}
