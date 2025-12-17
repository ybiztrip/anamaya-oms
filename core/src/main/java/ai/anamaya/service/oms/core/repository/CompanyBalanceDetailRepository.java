package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.CompanyBalanceDetail;
import ai.anamaya.service.oms.core.enums.BalanceSourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyBalanceDetailRepository extends JpaRepository<CompanyBalanceDetail, Long> {

    List<CompanyBalanceDetail> findByBalanceId(Long balanceId);

    Page<CompanyBalanceDetail> findByBalanceId(Long balanceId, Pageable pageable);

    List<CompanyBalanceDetail> findByReferenceIdAndSourceType(Long referenceId, BalanceSourceType sourceType);

    List<CompanyBalanceDetail> findByReferenceCodeAndSourceType(String referenceCode, BalanceSourceType sourceType);

    Page<CompanyBalanceDetail> findByBalanceIdOrderByCreatedAtDesc(Long balanceId, Pageable pageable);
}
