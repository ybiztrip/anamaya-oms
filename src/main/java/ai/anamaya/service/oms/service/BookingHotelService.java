package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.BookingHotelRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.entity.Booking;
import ai.anamaya.service.oms.entity.BookingHotel;
import ai.anamaya.service.oms.exception.AccessDeniedException;
import ai.anamaya.service.oms.exception.NotFoundException;
import ai.anamaya.service.oms.repository.BookingHotelRepository;
import ai.anamaya.service.oms.repository.BookingRepository;
import ai.anamaya.service.oms.security.JwtUtils;
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
    public ApiResponse<?> updateBookingHotels(Long bookingId, List<BookingHotelRequest> requests) {
        Long userId = jwtUtils.getUserIdFromToken();

        Booking booking = bookingService.getValidatedBooking(bookingId);

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

        return ApiResponse.success(bookingService.toResponse(booking, true, true));
    }
}
