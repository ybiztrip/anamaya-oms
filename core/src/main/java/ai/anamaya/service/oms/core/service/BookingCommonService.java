package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.apricode.AppricodeService;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.BalanceAdjustRequest;
import ai.anamaya.service.oms.core.entity.*;
import ai.anamaya.service.oms.core.enums.*;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingCommonService {

    private final BalanceService balanceService;
    private final BookingRepository bookingRepository;
    private final AppricodeService appricodeClient;
    private final UserService userService;

    public Booking getValidatedBookingById(CallerContext callerContext, Boolean isSystem, Long id) {
        Long companyId = callerContext.companyId();

        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!isSystem && !booking.getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You are not authorized to access this booking");
        }

        return booking;
    }

    public void bookingDebitBalance(
        CallerContext callerContext,
        Booking booking,
        List<BookingFlight> bookingFlights,
        BookingHotel bookingHotel
    ) {

        String referenceCode = "";
        BigDecimal flightTotalAmount = BigDecimal.ZERO;
        if (bookingFlights != null) {
            flightTotalAmount = bookingFlights.stream()
                .filter(f -> f.getStatus() == BookingFlightStatus.CREATED)
                .map(BookingFlight::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            referenceCode = bookingFlights.get(0).getBookingCode();
        }

        BigDecimal hotelTotalAmount = BigDecimal.ZERO;
        if (bookingHotel != null) {
            hotelTotalAmount = BigDecimal.valueOf(bookingHotel.getPartnerSellAmount());
            referenceCode = bookingHotel.getBookingCode();
        }

        if (referenceCode.isEmpty()) {
            throw new IllegalArgumentException("reference code is empty.");
        }

        List<CompanyBalanceDetail> balanceDetails  = balanceService.getBalanceDetailByReferenceCode(BalanceSourceType.BOOKING, referenceCode);
        if(balanceDetails != null && !balanceDetails.isEmpty()) {
            return;
        }

        if (bookingFlights != null) {
            balanceService.adjustBalance(
                callerContext,
                BalanceAdjustRequest.builder()
                    .companyId(booking.getCompanyId())
                    .code(BalanceCodeType.WALLET_FLIGHT)
                    .sourceType(BalanceSourceType.BOOKING)
                    .type(BalanceTransactionType.DEBIT)
                    .amount(flightTotalAmount)
                    .referenceId(booking.getId())
                    .referenceCode(referenceCode)
                    .remarks("Buying flight ticket approved by" + booking.getApprovedByName())
                    .build());
        }

        if (bookingHotel != null) {
            balanceService.adjustBalance(
                callerContext,
                BalanceAdjustRequest.builder()
                    .companyId(booking.getCompanyId())
                    .code(BalanceCodeType.WALLET_HOTEL)
                    .sourceType(BalanceSourceType.BOOKING)
                    .type(BalanceTransactionType.DEBIT)
                    .amount(hotelTotalAmount)
                    .referenceId(booking.getId())
                    .referenceCode(referenceCode)
                    .remarks("Buying hotel ticket approved by" + booking.getApprovedByName())
                    .build());
        }
    }

    public void sendNotificationToApprover(
        CallerContext callerContext,
        Long bookingId,
        List<BookingFlight> bookingFlights,
        List<BookingHotel> bookingHotels
    ) {
        if (bookingFlights != null &&
            bookingFlights.stream()
                .anyMatch(flight -> flight.getStatus() != BookingFlightStatus.BOOKED)) {
            return;
        }

        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new NotFoundException("Booking not found"));

        Optional<User> user = userService.getByEmail(callerContext, booking.getContactEmail());
        if (user.isEmpty()
            || !Boolean.TRUE.equals(user.get().getEnableChatEngine())) {
            return;
        }

        List<User> userApprover = userService.getListUserApprover(callerContext);
        List<User> userApproverNotification =
            Optional.ofNullable(userApprover)
                .orElse(List.of())
                .stream()
                .filter(u -> Boolean.TRUE.equals(u.getEnableChatEngine()))
                .toList();

        userApproverNotification.forEach(approver ->
            appricodeClient.approvalRequest(
                user.get(),
                List.of(approver),
                bookingId,
                bookingFlights,
                bookingHotels
            )
        );
    }

    public void sendNotificationToUser(
        User user,
        Long bookingId,
        List<BookingFlight> bookingFlights,
        List<BookingHotel> bookingHotels
    ) {
        appricodeClient.approvalResponse(user, bookingId, bookingFlights, bookingHotels);
    }

}
