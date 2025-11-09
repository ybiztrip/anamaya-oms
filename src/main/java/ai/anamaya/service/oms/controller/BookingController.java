package ai.anamaya.service.oms.controller;

import ai.anamaya.service.oms.dto.request.BookingPaxRequest;
import ai.anamaya.service.oms.dto.request.BookingRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.BookingResponse;
import ai.anamaya.service.oms.service.BookingPaxService;
import ai.anamaya.service.oms.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingPaxService bookingPaxService;

    @PostMapping
    public ApiResponse<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }

    @PutMapping("/{id}")
    public ApiResponse<BookingResponse> updateBookingPax(
            @PathVariable("id") Long bookingId,
            @RequestBody List<BookingPaxRequest> paxRequests
    ) {
        return bookingPaxService.updateBookingPax(bookingId, paxRequests);
    }

}
