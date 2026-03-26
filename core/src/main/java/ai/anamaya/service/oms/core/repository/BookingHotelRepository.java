package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingHotelRepository extends JpaRepository<BookingHotel, Long>, JpaSpecificationExecutor<BookingHotel> {
    List<BookingHotel> findByBookingIdAndIdIn(Long bookingId, List<Long> ids);
    List<BookingHotel> findByBookingId(Long bookingId);
    List<BookingHotel> findByBookingIdAndBookingCode(Long bookingId, String bookingCode);

    @Modifying
    @Query("""
        UPDATE BookingHotel bh
        SET bh.status = :status
        WHERE bh.otaReference = :otaReference
    """)
    int updateStatusByOtaReference(
        @Param("otaReference") String otaReference,
        @Param("status") BookingHotelStatus status
    );

}
