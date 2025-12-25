package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.BookingFlightListFilter;
import ai.anamaya.service.oms.core.dto.request.BookingHotelListFilter;
import ai.anamaya.service.oms.core.dto.request.BookingListFilter;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.*;
import ai.anamaya.service.oms.rest.dto.request.*;
import ai.anamaya.service.oms.rest.dto.response.BookingFlightResponseRest;
import ai.anamaya.service.oms.rest.dto.response.BookingHotelResponseRest;
import ai.anamaya.service.oms.rest.dto.response.BookingResponseRest;
import ai.anamaya.service.oms.rest.mapper.BookingMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingApproveService bookingApproveService;
    private final BookingFlightService bookingFlightService;
    private final BookingHotelService bookingHotelService;
    private final BookingPaxService bookingPaxService;

    private final BookingMapper mapper;
    private final JwtUtils jwtUtils;

    @PostMapping
    public ApiResponse<BookingResponseRest> createBooking(
        @Valid @RequestBody BookingRequestRest reqRest) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        var reqCore = mapper.toCore(reqRest);
        var resultCore = bookingService.createBooking(userCallerContext, reqCore);

        return ApiResponse.success(mapper.toRest(resultCore));
    }

    @GetMapping
    public ApiResponse<List<BookingResponseRest>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort,
        @ModelAttribute BookingListFilter filter
    ) {
        var pageResult = bookingService.getAll(page, size, sort, filter);

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
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        var resultCore = bookingService.getBookingById(userCallerContext, id);
        return ApiResponse.success(mapper.toRest(resultCore));
    }

    @GetMapping("/flights")
    public ApiResponse<List<BookingFlightResponseRest>> getListBookingFlights(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort,
        @ModelAttribute BookingFlightListFilter filter
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        boolean isSuperAdmin = authorities.stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            Long companyIdFromToken = jwtUtils.getCompanyIdFromToken();
            filter.setCompanyId(companyIdFromToken);
        }

        var pageResult = bookingFlightService.getAll(page, size, sort, filter);

        List<BookingFlightResponseRest> listRest = pageResult
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

    @PostMapping("/{id}/flights")
    public ApiResponse<?> submitBookingFlights(
        @PathVariable Long id,
        @RequestBody BookingFlightSubmitRequestRest requestRest) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        var request = mapper.toCoreSubmitFlights(requestRest);
        var resultCore = bookingFlightService.submitBookingFlights(userCallerContext, id, request);

        return ApiResponse.success(resultCore);
    }

    @GetMapping("/hotels")
    public ApiResponse<List<BookingHotelResponseRest>> getListBookingHotels(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort,
        @ModelAttribute BookingHotelListFilter filter
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        boolean isSuperAdmin = authorities.stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isSuperAdmin) {
            Long companyIdFromToken = jwtUtils.getCompanyIdFromToken();
            filter.setCompanyId(companyIdFromToken);
        }

        var pageResult = bookingHotelService.getAll(page, size, sort, filter);

        List<BookingHotelResponseRest> listRest = pageResult
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

    @PostMapping("/{id}/hotels")
    public ApiResponse<?> submitBookingHotels(
        @PathVariable Long id,
        @Valid @RequestBody BookingHotelSubmitRequestRest requestRest) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        var request = mapper.toCoreSubmitHotel(requestRest);
        var resultCore = bookingHotelService.submitBookingHotel(userCallerContext, id, request);

        return ApiResponse.success(resultCore);
    }

    @PreAuthorize("hasAnyRole('COMPANY_ADMIN')")
    @PutMapping("/{id}/approve")
    public ApiResponse<String> approve(
        @PathVariable Long id,
        @Valid @RequestBody BookingApproveRequestRest requestRest
    ) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        var request = mapper.toCoreApprove(requestRest);
        return ApiResponse.success(
            bookingApproveService.approveBooking(userCallerContext, id, request)
        );
    }

}
