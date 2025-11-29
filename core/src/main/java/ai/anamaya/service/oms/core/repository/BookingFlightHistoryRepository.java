package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.BookingFlightHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingFlightHistoryRepository extends JpaRepository<BookingFlightHistory, Long> {
    List<BookingFlightHistory> findByBookingId(Long bookingId);
}
