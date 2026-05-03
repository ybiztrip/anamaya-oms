package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.apricode.AppricodeService;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.BalanceAdjustRequest;
import ai.anamaya.service.oms.core.dto.request.CreditAdjustRequest;
import ai.anamaya.service.oms.core.entity.*;
import ai.anamaya.service.oms.core.enums.*;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.BookingRepository;
import ai.anamaya.service.oms.core.repository.BookingTravelPolicyRepository;
import ai.anamaya.service.oms.core.repository.CompanyConfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingCommonService {

    private final BalanceService balanceService;
    private final CreditService creditService;
    private final BookingRepository bookingRepository;
    private final BookingTravelPolicyRepository bookingTravelPolicyRepository;
    private final CompanyConfigRepository companyConfigRepository;
    private final AppricodeService appricodeClient;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public boolean validateBookingPaymentMethod(CallerContext callerContext, BookingPaymentMethod paymentMethod) {
        return companyConfigRepository
            .findByCompanyIdAndCode(callerContext.companyId(), "PAYMENT_METHOD")
            .map(config -> Arrays.stream(config.getValueStr().split(","))
                .map(String::trim)
                .anyMatch(method -> method.equals(paymentMethod.name()))
            )
            .orElse(false);
    }

    public Booking getValidatedBookingById(CallerContext callerContext, Boolean isSystem, Long id) {
        Long companyId = callerContext.companyId();

        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!isSystem && !booking.getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You are not authorized to access this booking");
        }

        return booking;
    }

    public void bookingTravelPolicy(
        CallerContext callerContext,
        String bookingCode,
        BookingType type
    ) {
        Long userId = callerContext.userId();
        TravelPolicy travelPolicy = userService.getTravelPolicy(callerContext);
        JsonNode jsonData = objectMapper.valueToTree(travelPolicy);
        BookingTravelPolicy bookingTravelPolicy = BookingTravelPolicy.builder()
            .companyId(travelPolicy.getCompanyId())
            .type(type)
            .bookingCode(bookingCode)
            .travelPolicyId(travelPolicy.getId())
            .travelPolicyName(travelPolicy.getName())
            .data(jsonData)
            .createdBy(userId)
            .updatedBy(userId)
            .build();

        bookingTravelPolicyRepository.save(bookingTravelPolicy);
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
                .filter(f -> f.getStatus() == BookingFlightStatus.APPROVED)
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
                    .remarks("Buying flight ticket approved by " + booking.getApprovedByName())
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
                    .remarks("Buying hotel ticket approved by " + booking.getApprovedByName())
                    .build());
        }
    }

    public void bookingRollbackBalance(
        CallerContext callerContext,
        Booking booking,
        List<BookingFlight> bookingFlights,
        BookingHotel bookingHotel
    ) {
        String referenceCode = "";
        if (bookingFlights != null) {
            referenceCode = bookingFlights.get(0).getBookingCode();
        }

        if (bookingHotel != null) {
            referenceCode = bookingHotel.getBookingCode();
        }

        List<CompanyBalanceDetail> companyBalanceDetails = balanceService.getBalanceDetailByReferenceCode(
            BalanceSourceType.BOOKING,
            referenceCode
        );


        Set<String> creditedReferenceCodes = companyBalanceDetails.stream()
            .filter(detail -> detail.getType() == BalanceTransactionType.CREDIT)
            .map(CompanyBalanceDetail::getReferenceCode)
            .collect(Collectors.toSet());

        List<CompanyBalanceDetail> balanceNeedRollback = companyBalanceDetails.stream()
            .filter(detail -> detail.getType() == BalanceTransactionType.DEBIT)
            .filter(detail -> !creditedReferenceCodes.contains(detail.getReferenceCode()))
            .toList();

        for(CompanyBalanceDetail detail: balanceNeedRollback) {
            balanceService.adjustBalance(
                callerContext,
                BalanceAdjustRequest.builder()
                    .companyId(booking.getCompanyId())
                    .code(detail.getBalance().getCode())
                    .sourceType(detail.getSourceType())
                    .type(BalanceTransactionType.CREDIT)
                    .amount(detail.getAmount())
                    .referenceId(detail.getReferenceId())
                    .referenceCode(detail.getReferenceCode())
                    .remarks("Rollback by system")
                    .build());
        }
    }

    public void bookingDebitCredit(
        CallerContext callerContext,
        Booking booking,
        List<BookingFlight> bookingFlights,
        BookingHotel bookingHotel
    ) {

        String referenceCode = "";
        BigDecimal flightTotalAmount = BigDecimal.ZERO;
        if (bookingFlights != null) {
            flightTotalAmount = bookingFlights.stream()
                .filter(f -> f.getStatus() == BookingFlightStatus.APPROVED)
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

        List<CompanyCreditDetail> balanceDetails  = creditService.getBalanceDetailByReferenceCode(CreditSourceType.BOOKING, referenceCode);
        if(balanceDetails != null && !balanceDetails.isEmpty()) {
            return;
        }

        if (bookingFlights != null) {
            creditService.adjustBalance(
                callerContext,
                CreditAdjustRequest.builder()
                    .companyId(booking.getCompanyId())
                    .code(CreditCodeType.CREDIT_FLIGHT)
                    .sourceType(CreditSourceType.BOOKING)
                    .type(CreditTransactionType.DEBIT)
                    .amount(flightTotalAmount)
                    .referenceId(booking.getId())
                    .referenceCode(referenceCode)
                    .remarks("Buying flight ticket approved by " + booking.getApprovedByName())
                    .build());
        }

        if (bookingHotel != null) {
            creditService.adjustBalance(
                callerContext,
                CreditAdjustRequest.builder()
                    .companyId(booking.getCompanyId())
                    .code(CreditCodeType.CREDIT_HOTEL)
                    .sourceType(CreditSourceType.BOOKING)
                    .type(CreditTransactionType.DEBIT)
                    .amount(hotelTotalAmount)
                    .referenceId(booking.getId())
                    .referenceCode(referenceCode)
                    .remarks("Buying hotel ticket approved by " + booking.getApprovedByName())
                    .build());
        }
    }

    public void bookingRollbackCredit(
        CallerContext callerContext,
        Booking booking,
        List<BookingFlight> bookingFlights,
        BookingHotel bookingHotel
    ) {
        String referenceCode = "";
        if (bookingFlights != null) {
            referenceCode = bookingFlights.get(0).getBookingCode();
        }

        if (bookingHotel != null) {
            referenceCode = bookingHotel.getBookingCode();
        }

        List<CompanyCreditDetail> companyBalanceDetails = creditService.getBalanceDetailByReferenceCode(
            CreditSourceType.BOOKING,
            referenceCode
        );


        Set<String> creditedReferenceCodes = companyBalanceDetails.stream()
            .filter(detail -> detail.getType() == CreditTransactionType.CREDIT)
            .map(CompanyCreditDetail::getReferenceCode)
            .collect(Collectors.toSet());

        List<CompanyCreditDetail> balanceNeedRollback = companyBalanceDetails.stream()
            .filter(detail -> detail.getType() == CreditTransactionType.DEBIT)
            .filter(detail -> !creditedReferenceCodes.contains(detail.getReferenceCode()))
            .toList();

        for(CompanyCreditDetail detail: balanceNeedRollback) {
            creditService.adjustBalance(
                callerContext,
                CreditAdjustRequest.builder()
                    .companyId(booking.getCompanyId())
                    .code(detail.getBalance().getCode())
                    .sourceType(detail.getSourceType())
                    .type(CreditTransactionType.CREDIT)
                    .amount(detail.getAmount())
                    .referenceId(detail.getReferenceId())
                    .referenceCode(detail.getReferenceCode())
                    .remarks("Rollback by system")
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

    public List<BookingApproval> buildApprovals(
        Long bookingId,
        BookingType type,
        List<Long> bookingChildIds,
        ApprovalAction action,
        Long userId,
        String userEmail,
        String notes
    ) {
        return bookingChildIds.stream()
            .map(id -> BookingApproval.builder()
                .bookingId(bookingId)
                .bookingType(type)
                .bookingChildId(id)
                .action(action)
                .createdBy(userId)
                .createdByName(userEmail)
                .notes(notes)
                .build()
            )
            .collect(Collectors.toList());
    }

}
