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

@Repository
public interface BookingApprovalRepository extends JpaRepository<BookingApproval, Long>, JpaSpecificationExecutor<BookingApproval> {
    @Query("""
        SELECT DISTINCT ba.bookingId
        FROM BookingApproval ba
        WHERE ba.createdBy = :userId
        AND ba.action = :action
    """)
    Page<Long> findMyApprovedBookingIds(
        @Param("userId") Long userId,
        @Param("action") ApprovalAction action,
        Pageable pageable
    );
}
