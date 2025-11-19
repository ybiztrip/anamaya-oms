package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.dto.request.BookingFlightRequest;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.BookingResponse;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.enums.BookingStatus;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.BookingFlightRepository;
import ai.anamaya.service.oms.core.repository.BookingRepository;
import ai.anamaya.service.oms.core.security.JwtUtils;
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
    public BookingResponse updateBookingFlights(Long bookingId, List<BookingFlightRequest> requests) {
        Long userId = jwtUtils.getUserIdFromToken();

        Booking booking = bookingService.getValidatedBooking(bookingId);

        if (!booking.getStatus().equals(BookingStatus.CREATED)) {
            throw new AccessDeniedException("This booking can no longer be updated.");
        }

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

        return bookingService.toResponse(booking, true, true);
    }
}
