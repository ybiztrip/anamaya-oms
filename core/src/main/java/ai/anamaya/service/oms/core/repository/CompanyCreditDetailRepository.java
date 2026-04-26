package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.CompanyCreditDetail;
import ai.anamaya.service.oms.core.enums.CreditSourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyCreditDetailRepository extends JpaRepository<CompanyCreditDetail, Long> {

    List<CompanyCreditDetail> findByBalanceId(Long balanceId);

    Page<CompanyCreditDetail> findByBalanceId(Long balanceId, Pageable pageable);

    List<CompanyCreditDetail> findByReferenceIdAndSourceType(Long referenceId, CreditSourceType sourceType);

    List<CompanyCreditDetail> findByReferenceCodeAndSourceType(String referenceCode, CreditSourceType sourceType);

    Page<CompanyCreditDetail> findByBalanceIdOrderByCreatedAtDesc(Long balanceId, Pageable pageable);
}
