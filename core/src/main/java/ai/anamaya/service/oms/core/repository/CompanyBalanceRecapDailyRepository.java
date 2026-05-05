package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.CompanyBalanceRecapDaily;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CompanyBalanceRecapDailyRepository extends JpaRepository<CompanyBalanceRecapDaily, Long> {

    Optional<CompanyBalanceRecapDaily> findByBalanceIdAndRecapDate(Long balanceId, LocalDate recapDate);

    @Query("SELECT r FROM CompanyBalanceRecapDaily r WHERE r.balance.id = :balanceId AND r.recapDate < :date ORDER BY r.recapDate DESC")
    List<CompanyBalanceRecapDaily> findLatestBeforeDate(@Param("balanceId") Long balanceId, @Param("date") LocalDate date, Pageable pageable);
}