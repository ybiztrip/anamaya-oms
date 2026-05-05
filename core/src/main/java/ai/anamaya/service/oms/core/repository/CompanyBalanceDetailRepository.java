package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.CompanyBalanceDetail;
import ai.anamaya.service.oms.core.enums.BalanceSourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CompanyBalanceDetailRepository extends JpaRepository<CompanyBalanceDetail, Long> {

    List<CompanyBalanceDetail> findByBalanceId(Long balanceId);

    Page<CompanyBalanceDetail> findByBalanceId(Long balanceId, Pageable pageable);

    List<CompanyBalanceDetail> findByReferenceIdAndSourceType(Long referenceId, BalanceSourceType sourceType);

    List<CompanyBalanceDetail> findByReferenceCodeAndSourceType(String referenceCode, BalanceSourceType sourceType);

    Page<CompanyBalanceDetail> findByBalanceIdOrderByCreatedAtDesc(Long balanceId, Pageable pageable);

    @Query("SELECT d FROM CompanyBalanceDetail d WHERE d.balance.id = :balanceId AND DATE(d.createdAt) = :date ORDER BY d.createdAt ASC")
    List<CompanyBalanceDetail> findByBalanceIdAndDate(@Param("balanceId") Long balanceId, @Param("date") LocalDate date);

    @Query("SELECT d FROM CompanyBalanceDetail d WHERE d.balance.id = :balanceId AND DATE(d.createdAt) < :date ORDER BY d.createdAt DESC")
    List<CompanyBalanceDetail> findLatestBeforeDate(@Param("balanceId") Long balanceId, @Param("date") LocalDate date, Pageable pageable);
}
