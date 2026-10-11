package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.BookingTrain;
import ai.anamaya.service.oms.core.enums.BookingTrainStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingTrainRepository extends JpaRepository<BookingTrain, Long> {
    List<BookingTrain> findByBookingId(Long bookingId);
    List<BookingTrain> findByBookingIdAndIdIn(Long bookingId, List<Long> ids);
    Optional<BookingTrain> findByIdAndBookingId(Long id, Long bookingId);
    Optional<BookingTrain> findFirstByBookingIdAndBookingCode(Long bookingId, String bookingCode);

    // ponytail: workers take 100 rows per tick; page through if train volume grows past that per minute
    List<BookingTrain> findTop100ByStatusOrderByIdAsc(BookingTrainStatus status);
    List<BookingTrain> findTop100ByStatusInAndTimeLimitBeforeOrderByIdAsc(Collection<BookingTrainStatus> statuses, LocalDateTime time);
}
