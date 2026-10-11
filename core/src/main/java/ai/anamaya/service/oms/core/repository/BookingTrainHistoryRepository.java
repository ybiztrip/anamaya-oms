package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.BookingTrainHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingTrainHistoryRepository extends JpaRepository<BookingTrainHistory, Long> {
}
