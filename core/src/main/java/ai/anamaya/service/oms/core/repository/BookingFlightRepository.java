package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingFlightRepository extends JpaRepository<BookingFlight, Long>, JpaSpecificationExecutor<BookingFlight> {
    List<BookingFlight> findByBookingIdAndIdIn(Long bookingId, List<Long> ids);
    List<BookingFlight> findByBookingId(Long bookingId);
    List<BookingFlight> findByBookingIdAndBookingCode(Long bookingId, String bookingCode);
    List<BookingFlight> findByBookingCode(String bookingCode);

    @Modifying
    @Query("""
        UPDATE BookingFlight bf
        SET bf.status = :status
        WHERE bf.bookingId = :bookingId
          AND bf.bookingReference IN :bookingReferenceIds
    """)
    int updateStatusByBookingReferences(
        @Param("bookingId") Long bookingId,
        @Param("bookingReferenceIds") List<String> bookingReferenceIds,
        @Param("status") BookingFlightStatus status
    );

    @Modifying
    @Query("""
        UPDATE BookingFlight bf
        SET bf.status = :status
        WHERE bf.bookingCode = :bookingCode
    """)
    int updateStatusByBookingCode(
        @Param("bookingCode") String bookingCode,
        @Param("status") BookingFlightStatus status
    );
}
