package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.BookingAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BookingAttachmentRepository extends JpaRepository<BookingAttachment, Long>, JpaSpecificationExecutor<BookingAttachment> {
    List<BookingAttachment> findByBookingId(Long bookingId);
}
