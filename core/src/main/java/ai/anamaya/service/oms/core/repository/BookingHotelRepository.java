package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.BookingHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BookingHotelRepository extends JpaRepository<BookingHotel, Long>, JpaSpecificationExecutor<BookingHotel> {
    List<BookingHotel> findByBookingIdAndIdIn(Long bookingId, List<Long> ids);
    List<BookingHotel> findByBookingId(Long bookingId);
    List<BookingHotel> findByBookingIdAndBookingCode(Long bookingId, String bookingCode);
}
