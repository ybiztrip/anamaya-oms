package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.BookingFlightRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.entity.Booking;
import ai.anamaya.service.oms.entity.BookingFlight;
import ai.anamaya.service.oms.exception.AccessDeniedException;
import ai.anamaya.service.oms.exception.NotFoundException;
import ai.anamaya.service.oms.repository.BookingFlightRepository;
import ai.anamaya.service.oms.repository.BookingRepository;
import ai.anamaya.service.oms.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingFlightService {

    private final BookingRepository bookingRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final JwtUtils jwtUtils;
    private final BookingService bookingService;

    @Transactional
    public ApiResponse<?> updateBookingFlights(Long bookingId, List<BookingFlightRequest> requests) {
        Long userId = jwtUtils.getUserIdFromToken();

        Booking booking = bookingService.getValidatedBooking(bookingId);

        for (BookingFlightRequest req : requests) {
            if (req.getId() != null) {
                bookingFlightRepository.findById(req.getId()).ifPresent(existing -> {
                    if (req.isDeleted()) {
                        bookingFlightRepository.delete(existing);
                    } else {
                        existing.setType(req.getType());
                        existing.setClientSource(req.getClientSource());
                        existing.setItemId(req.getItemId());
                        existing.setOrigin(req.getOrigin());
                        existing.setDestination(req.getDestination());
                        existing.setDepartureDatetime(req.getDepartureDatetime());
                        existing.setArrivalDatetime(req.getArrivalDatetime());
                        existing.setStatus(req.getStatus());
                        existing.setUpdatedBy(userId);
                        bookingFlightRepository.save(existing);
                    }
                });
            } else if (!req.isDeleted()) {
                BookingFlight newFlight = BookingFlight.builder()
                        .bookingId(bookingId)
                        .type(req.getType())
                        .clientSource(req.getClientSource())
                        .itemId(req.getItemId())
                        .origin(req.getOrigin())
                        .destination(req.getDestination())
                        .departureDatetime(req.getDepartureDatetime())
                        .arrivalDatetime(req.getArrivalDatetime())
                        .status(req.getStatus())
                        .createdBy(userId)
                        .updatedBy(userId)
                        .build();
                bookingFlightRepository.save(newFlight);
            }
        }

        return ApiResponse.success(bookingService.toResponse(booking, true, true));
    }
}
