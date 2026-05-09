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
    List<BookingFlight> findByBookingIdIn(List<Long> bookingId);
    List<BookingFlight> findByBookingId(Long bookingId);
    List<BookingFlight> findByBookingIdAndBookingCode(Long bookingId, String bookingCode);
    List<BookingFlight> findByBookingCode(String bookingCode);
    List<BookingFlight> findByIdInAndCompanyId(List<Long> ids, Long companyId);

    @Modifying
    @Query("""
        UPDATE BookingFlight bf
        SET bf.invoiceId = :invoiceId, bf.updatedBy = :userId
        WHERE bf.id IN :ids
          AND bf.companyId = :companyId
          AND bf.invoiceId IS NULL
    """)
    int linkInvoice(
        @Param("ids") List<Long> ids,
        @Param("companyId") Long companyId,
        @Param("invoiceId") Long invoiceId,
        @Param("userId") Long userId
    );

    @Modifying
    @Query("""
        UPDATE BookingFlight bf
        SET bf.invoiceId = NULL, bf.updatedBy = :userId
        WHERE bf.invoiceId = :invoiceId
    """)
    int unlinkInvoice(
        @Param("invoiceId") Long invoiceId,
        @Param("userId") Long userId
    );

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
