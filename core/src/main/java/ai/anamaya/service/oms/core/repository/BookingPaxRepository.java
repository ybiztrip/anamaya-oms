package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.BookingPax;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface BookingPaxRepository extends JpaRepository<BookingPax, Long> {
    List<BookingPax> findByBookingId(Long bookingId);
    List<BookingPax> findByBookingIdAndBookingCode(Long bookingId, String bookingCode);
    List<BookingPax> findByBookingCodeIn(Collection<String> bookingCodes);
}
