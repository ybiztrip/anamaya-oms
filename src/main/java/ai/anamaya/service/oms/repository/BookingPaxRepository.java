package ai.anamaya.service.oms.repository;

import ai.anamaya.service.oms.entity.BookingPax;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingPaxRepository extends JpaRepository<BookingPax, Long> {
    List<BookingPax> findByBookingId(Long bookingId);
}
