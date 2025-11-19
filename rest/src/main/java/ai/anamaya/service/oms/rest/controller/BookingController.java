package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.BookingResponse;
import ai.anamaya.service.oms.core.service.*;
import ai.anamaya.service.oms.rest.dto.request.BookingFlightRequestRest;
import ai.anamaya.service.oms.rest.dto.request.BookingHotelRequestRest;
import ai.anamaya.service.oms.rest.dto.request.BookingPaxRequestRest;
import ai.anamaya.service.oms.rest.dto.request.BookingRequestRest;
import ai.anamaya.service.oms.rest.dto.response.BookingResponseRest;
import ai.anamaya.service.oms.rest.dto.response.UserResponseRest;
import ai.anamaya.service.oms.rest.mapper.BookingMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingApproveService bookingApproveService;
    private final BookingSubmitService bookingSubmitService;
    private final BookingFlightService bookingFlightService;
    private final BookingHotelService bookingHotelService;
    private final BookingPaxService bookingPaxService;

    private final BookingMapper mapper;

    @PostMapping
    public ApiResponse<BookingResponseRest> createBooking(
        @Valid @RequestBody BookingRequestRest reqRest) {

        var reqCore = mapper.toCore(reqRest);
        var resultCore = bookingService.createBooking(reqCore);

        return ApiResponse.success(mapper.toRest(resultCore));
    }

    @PutMapping("/{id}")
    public ApiResponse<BookingResponseRest> updateBookingPax(
        @PathVariable Long id,
        @RequestBody List<BookingPaxRequestRest> paxRest) {

        var paxCore = mapper.toCorePax(paxRest);

        var resultCore = bookingPaxService.updateBookingPax(id, paxCore);
        return ApiResponse.success(mapper.toRest(resultCore));
    }

    @GetMapping
    public ApiResponse<List<BookingResponseRest>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort
    ) {
        var pageResult = bookingService.getAll(page, size, sort);

        List<BookingResponseRest> listRest = pageResult
            .getContent()
            .stream()
            .map(mapper::toRest)
            .toList();

        return ApiResponse.paginatedSuccess(
            listRest,
            pageResult.getTotalElements(),
            pageResult.getTotalPages(),
            pageResult.isLast(),
            pageResult.getSize(),
            pageResult.getNumber()
        );
    }


    @GetMapping("/{id}")
    public ApiResponse<BookingResponseRest> getBooking(@PathVariable Long id) {
        var resultCore = bookingService.getBookingById(id);
        return ApiResponse.success(mapper.toRest(resultCore));
    }

    @PutMapping("/{id}/flights")
    public ApiResponse<?> updateBookingFlights(
        @PathVariable Long id,
        @RequestBody List<BookingFlightRequestRest> restList) {

        var listCore = mapper.toCoreFlights(restList);
        var resultCore = bookingFlightService.updateBookingFlights(id, listCore);

        return ApiResponse.success(resultCore);
    }

    @PutMapping("/{id}/hotels")
    public ApiResponse<?> updateBookingHotels(
        @PathVariable Long id,
        @RequestBody List<BookingHotelRequestRest> restList) {
        var listCore = mapper.toCoreHotels(restList);
        var resultCore = bookingHotelService.updateBookingHotels(id, listCore);

        return ApiResponse.success(resultCore);
    }

    @PutMapping("/{id}/submit")
    public ApiResponse<?> submitBooking(@PathVariable Long id) {
        var result = bookingSubmitService.submitBooking(id);
        return ApiResponse.success(result);
    }

    @PreAuthorize("hasAnyRole('COMPANY_ADMIN')")
    @PutMapping("/{id}/approve")
    public ApiResponse<String> approve(@PathVariable Long id) {
        return ApiResponse.success(
            bookingApproveService.approveBooking(id)
        );
    }
}
