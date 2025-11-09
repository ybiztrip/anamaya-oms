package ai.anamaya.service.oms.repository;

import ai.anamaya.service.oms.entity.BookingFlight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingFlightRepository extends JpaRepository<BookingFlight, Long> {
    List<BookingFlight> findByBookingId(Long bookingId);
}
