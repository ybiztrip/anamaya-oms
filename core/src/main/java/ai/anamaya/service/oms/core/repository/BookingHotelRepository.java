package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface BookingHotelRepository extends JpaRepository<BookingHotel, Long>, JpaSpecificationExecutor<BookingHotel> {
    List<BookingHotel> findByBookingIdAndIdIn(Long bookingId, List<Long> ids);
    List<BookingHotel> findByBookingIdIn(List<Long> bookingId);
    List<BookingHotel> findByBookingId(Long bookingId);
    List<BookingHotel> findByBookingIdAndBookingCode(Long bookingId, String bookingCode);
    List<BookingHotel> findByBookingCode(String bookingCode);
    List<BookingHotel> findByBookingCodeIn(Collection<String> bookingCodes);
    List<BookingHotel> findByIdInAndCompanyId(List<Long> ids, Long companyId);

    @Modifying
    @Query("""
        UPDATE BookingHotel bh
        SET bh.invoiceId = :invoiceId, bh.updatedBy = :userId
        WHERE bh.id IN :ids
          AND bh.companyId = :companyId
          AND bh.invoiceId IS NULL
    """)
    int linkInvoice(
        @Param("ids") List<Long> ids,
        @Param("companyId") Long companyId,
        @Param("invoiceId") Long invoiceId,
        @Param("userId") Long userId
    );

    @Modifying
    @Query("""
        UPDATE BookingHotel bh
        SET bh.invoiceId = NULL, bh.updatedBy = :userId
        WHERE bh.invoiceId = :invoiceId
    """)
    int unlinkInvoice(
        @Param("invoiceId") Long invoiceId,
        @Param("userId") Long userId
    );

    @Modifying
    @Query("""
        UPDATE BookingHotel bh
        SET bh.status = :status
        WHERE bh.bookingCode = :bookingCode
    """)
    int updateStatusByBookingCode(
        @Param("bookingCode") String bookingCode,
        @Param("status") BookingHotelStatus status
    );

    @Modifying
    @Query("""
        UPDATE BookingHotel bh
        SET bh.refundId = :refundId, bh.updatedBy = :userId
        WHERE bh.id = :id
          AND bh.companyId = :companyId
          AND bh.refundId IS NULL
    """)
    int linkRefund(
        @Param("id") Long id,
        @Param("companyId") Long companyId,
        @Param("refundId") Long refundId,
        @Param("userId") Long userId
    );

    @Modifying
    @Query("""
        UPDATE BookingHotel bh
        SET bh.refundId = NULL, bh.updatedBy = :userId
        WHERE bh.refundId = :refundId
    """)
    int unlinkRefund(
        @Param("refundId") Long refundId,
        @Param("userId") Long userId
    );

    java.util.Optional<BookingHotel> findFirstByRefundId(Long refundId);

    @Modifying
    @Query("""
        UPDATE BookingHotel bh
        SET bh.status = :status, bh.updatedBy = :userId
        WHERE bh.id = :id
    """)
    int updateStatusById(
        @Param("id") Long id,
        @Param("status") BookingHotelStatus status,
        @Param("userId") Long userId
    );

}
