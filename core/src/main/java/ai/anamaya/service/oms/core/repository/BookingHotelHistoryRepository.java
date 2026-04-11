package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.BookingHotelHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingHotelHistoryRepository extends JpaRepository<BookingHotelHistory, Long> {
    List<BookingHotelHistory> findByBookingId(Long bookingId);
}
