package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.dto.request.BookingHotelRequest;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingHotelService {

    private final BookingRepository bookingRepository;
    private final BookingHotelRepository bookingHotelRepository;
    private final JwtUtils jwtUtils;
    private final BookingService bookingService;

    @Transactional
    public BookingResponse updateBookingHotels(Long bookingId, List<BookingHotelRequest> requests) {
        Long userId = jwtUtils.getUserIdFromToken();

        Booking booking = bookingService.getValidatedBooking(bookingId);

        if (!booking.getStatus().equals(BookingStatus.CREATED)) {
            throw new AccessDeniedException("This booking can no longer be updated.");
        }

        for (BookingHotelRequest req : requests) {
            if (req.getId() != null) {
                bookingHotelRepository.findById(req.getId()).ifPresent(existing -> {
                    if (req.isDeleted()) {
                        bookingHotelRepository.delete(existing);
                    } else {
                        existing.setClientSource(req.getClientSource());
                        existing.setItemId(req.getItemId());
                        existing.setRateKey(req.getRateKey());
                        existing.setNumRoom(req.getNumRoom());
                        existing.setCheckInDate(req.getCheckInDate());
                        existing.setCheckOutDate(req.getCheckOutDate());
                        existing.setPartnerSellAmount(req.getPartnerSellAmount());
                        existing.setPartnerNettAmount(req.getPartnerNettAmount());
                        existing.setCurrency(req.getCurrency());
                        existing.setSpecialRequest(req.getSpecialRequest());
                        existing.setStatus(req.getStatus());
                        existing.setUpdatedBy(userId);
                        bookingHotelRepository.save(existing);
                    }
                });
            } else if (!req.isDeleted()) {
                BookingHotel newHotel = BookingHotel.builder()
                        .bookingId(bookingId)
                        .clientSource(req.getClientSource())
                        .itemId(req.getItemId())
                        .rateKey(req.getRateKey())
                        .numRoom(req.getNumRoom())
                        .checkInDate(req.getCheckInDate())
                        .checkOutDate(req.getCheckOutDate())
                        .partnerSellAmount(req.getPartnerSellAmount())
                        .partnerNettAmount(req.getPartnerNettAmount())
                        .currency(req.getCurrency())
                        .specialRequest(req.getSpecialRequest())
                        .status(req.getStatus())
                        .createdBy(userId)
                        .updatedBy(userId)
                        .build();
                bookingHotelRepository.save(newHotel);
            }
        }

        return bookingService.toResponse(booking, true, true);
    }
}
