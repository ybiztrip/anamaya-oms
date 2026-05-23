package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.Refund;
import ai.anamaya.service.oms.core.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long>, JpaSpecificationExecutor<Refund> {

    Optional<Refund> findByIdAndCompanyId(Long id, Long companyId);

    boolean existsByCompanyIdAndBookingCodeAndStatusNot(Long companyId, String bookingCode, RefundStatus status);

    boolean existsByCompanyIdAndCode(Long companyId, String code);
}
