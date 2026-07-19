package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.BookingApproval;
import ai.anamaya.service.oms.core.enums.ApprovalAction;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingApprovalRepository extends JpaRepository<BookingApproval, Long>, JpaSpecificationExecutor<BookingApproval> {
    @Query("""
    SELECT ba.bookingId
    FROM BookingApproval ba
    WHERE ba.createdBy = :userId
    AND (:actions IS NULL OR ba.action IN :actions)
    GROUP BY ba.bookingId
    ORDER BY MAX(ba.createdAt) DESC
    """)
    Page<Long> findMyBookingIds(
        @Param("userId") Long userId,
        @Param("actions") List<ApprovalAction> actions,
        Pageable pageable
    );
}
