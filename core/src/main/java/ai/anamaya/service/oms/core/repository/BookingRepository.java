package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.Booking;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    @Query("""
        SELECT DISTINCT b.id, b.createdAt FROM Booking b
        LEFT JOIN BookingFlight bf ON bf.booking.id = b.id
        LEFT JOIN BookingHotel bh ON bh.booking.id = b.id
        WHERE 
            b.status = 'CREATE'
            OR bf.status = 'BOOKED'
            OR bh.status = 'BOOKED'
    """)
    Page<Object[]> findBookingIdsNeedApproval(Pageable pageable);

}
