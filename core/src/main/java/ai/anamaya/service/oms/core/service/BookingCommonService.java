package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.dto.request.BalanceAdjustRequest;
import ai.anamaya.service.oms.core.entity.Booking;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.entity.CompanyBalanceDetail;
import ai.anamaya.service.oms.core.enums.BalanceCodeType;
import ai.anamaya.service.oms.core.enums.BalanceSourceType;
import ai.anamaya.service.oms.core.enums.BalanceTransactionType;
import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.BookingRepository;
import ai.anamaya.service.oms.core.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingCommonService {

    private final BalanceService balanceService;
    private final BookingRepository bookingRepository;
    private final JwtUtils jwtUtils;

    public Booking getValidatedBookingById(Boolean isSystem, Long id) {
        Long companyId = jwtUtils.getCompanyIdFromToken();

        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!isSystem && !booking.getCompanyId().equals(companyId)) {
            throw new AccessDeniedException("You are not authorized to access this booking");
        }

        return booking;
    }

    public void bookingDebitBalance(
        Booking booking,
        List<BookingFlight> bookingFlights,
        List<BookingHotel> bookingHotels
    ) {

        List<CompanyBalanceDetail> balanceDetails  = balanceService.getBalanceDetailByReference(BalanceSourceType.BOOKING, booking.getId());
        if(balanceDetails != null || !balanceDetails.isEmpty()) {
            return;
        }

        BigDecimal flightTotalAmount = bookingFlights.stream()
            .filter(f -> f.getStatus() == BookingFlightStatus.CREATED)
            .map(BookingFlight::getTotalAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal hotelTotalAmount = bookingHotels.stream()
            .filter(h -> h.getStatus() == 1)
            .map(this::calculateHotelAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        balanceService.adjustBalance(BalanceAdjustRequest.builder()
            .companyId(booking.getCompanyId())
            .code(BalanceCodeType.WALLET_FLIGHT)
            .sourceType(BalanceSourceType.BOOKING)
            .type(BalanceTransactionType.DEBIT)
            .amount(flightTotalAmount)
            .referenceId(booking.getId())
            .referenceCode(booking.getCode())
            .remarks("Buying flight ticket approved by"+booking.getApprovedByName())
            .build());

        balanceService.adjustBalance(BalanceAdjustRequest.builder()
            .companyId(booking.getCompanyId())
            .code(BalanceCodeType.WALLET_HOTEL)
            .sourceType(BalanceSourceType.BOOKING)
            .type(BalanceTransactionType.DEBIT)
            .amount(hotelTotalAmount)
            .referenceId(booking.getId())
            .referenceCode(booking.getCode())
            .remarks("Buying hotel ticket approved by"+booking.getApprovedByName())
            .build());
    }


    private BigDecimal calculateHotelAmount(BookingHotel h) {
        long nights = ChronoUnit.DAYS.between(h.getCheckInDate(), h.getCheckOutDate());

        if (nights <= 0 || h.getPartnerSellAmount() == null || h.getNumRoom() == null) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(h.getPartnerSellAmount())
            .multiply(BigDecimal.valueOf(h.getNumRoom()))
            .multiply(BigDecimal.valueOf(nights));
    }
}
