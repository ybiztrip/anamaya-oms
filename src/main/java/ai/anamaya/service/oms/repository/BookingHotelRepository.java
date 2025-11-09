package ai.anamaya.service.oms.repository;

import ai.anamaya.service.oms.entity.BookingHotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingHotelRepository extends JpaRepository<BookingHotel, Long> {
    List<BookingHotel> findByBookingId(Long bookingId);
}
