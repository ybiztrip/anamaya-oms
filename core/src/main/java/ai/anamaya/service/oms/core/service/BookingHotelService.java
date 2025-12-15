package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.BookingHotelRequest;
import ai.anamaya.service.oms.core.dto.request.BookingHotelSubmitRequest;
import ai.anamaya.service.oms.core.dto.response.BookingResponse;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.enums.BookingStatus;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.repository.BookingHotelRepository;
import ai.anamaya.service.oms.core.repository.BookingRepository;
import ai.anamaya.service.oms.core.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingHotelService {

    private final BookingRepository bookingRepository;
    private final BookingHotelRepository bookingHotelRepository;
    private final JwtUtils jwtUtils;
    private final BookingService bookingService;
    private final BookingPaxService bookingPaxService;

    @Transactional
    public BookingResponse submitBookingHotel(CallerContext callerContext, Long bookingId, BookingHotelSubmitRequest request) {
        Long userId = callerContext.userId();
        Booking booking = bookingService.getValidatedBooking(bookingId);

        if (!booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AccessDeniedException("This booking journey is not approved.");
        }

        BookingHotelRequest reqHotel = request.getHotel();
        String bookingCode = "ANMH:"+Instant.now().toEpochMilli();

        BookingHotel newHotel = BookingHotel.builder()
            .bookingId(bookingId)
            .bookingCode(bookingCode)
            .clientSource(reqHotel.getClientSource())
            .itemId(reqHotel.getItemId())
            .roomId(reqHotel.getRoomId())
            .rateKey(reqHotel.getRateKey())
            .numRoom(reqHotel.getNumRoom())
            .checkInDate(reqHotel.getCheckInDate())
            .checkOutDate(reqHotel.getCheckOutDate())
            .partnerSellAmount(reqHotel.getPartnerSellAmount())
            .partnerNettAmount(reqHotel.getPartnerNettAmount())
            .currency(reqHotel.getCurrency())
            .specialRequest(reqHotel.getSpecialRequest())
            .status(reqHotel.getStatus())
            .createdBy(userId)
            .updatedBy(userId)
            .build();
        bookingHotelRepository.save(newHotel);

        bookingPaxService.submitBookingPax(callerContext, bookingId, bookingCode, request.getPaxs());

        return bookingService.toResponse(booking, true, true);
    }

}
