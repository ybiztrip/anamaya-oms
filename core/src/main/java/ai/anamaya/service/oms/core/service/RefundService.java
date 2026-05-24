package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.BalanceAdjustRequest;
import ai.anamaya.service.oms.core.dto.request.CreditAdjustRequest;
import ai.anamaya.service.oms.core.dto.request.RefundCancelRequest;
import ai.anamaya.service.oms.core.dto.request.RefundCreateRequest;
import ai.anamaya.service.oms.core.dto.request.RefundFilter;
import ai.anamaya.service.oms.core.dto.request.RefundPaidRequest;
import ai.anamaya.service.oms.core.dto.request.RefundProviderRequest;
import ai.anamaya.service.oms.core.dto.response.RefundResponse;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.entity.Refund;
import ai.anamaya.service.oms.core.enums.BalanceCodeType;
import ai.anamaya.service.oms.core.enums.BalanceSourceType;
import ai.anamaya.service.oms.core.enums.BalanceTransactionType;
import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
import ai.anamaya.service.oms.core.enums.BookingType;
import ai.anamaya.service.oms.core.enums.CreditCodeType;
import ai.anamaya.service.oms.core.enums.CreditSourceType;
import ai.anamaya.service.oms.core.enums.CreditTransactionType;
import ai.anamaya.service.oms.core.enums.RefundStatus;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.BookingFlightRepository;
import ai.anamaya.service.oms.core.repository.BookingHotelRepository;
import ai.anamaya.service.oms.core.repository.RefundRepository;
import ai.anamaya.service.oms.core.specification.RefundSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final BookingHotelRepository bookingHotelRepository;
    private final BalanceService balanceService;
    private final CreditService creditService;
    private final java.util.Map<String, FlightProvider> flightProviders;
    private final java.util.Map<String, HotelProvider> hotelProviders;

    private FlightProvider getFlightProvider(String source) {
        String key = (source != null ? source.toLowerCase() : "biztrip") + "FlightProvider";
        FlightProvider provider = flightProviders.get(key);
        if (provider == null) {
            provider = flightProviders.get("biztripFlightProvider");
        }
        return provider;
    }

    private HotelProvider getHotelProvider(String source) {
        String key = (source != null ? source.toLowerCase() : "biztrip") + "HotelProvider";
        HotelProvider provider = hotelProviders.get(key);
        if (provider == null) {
            provider = hotelProviders.get("biztripHotelProvider");
        }
        return provider;
    }

    @Transactional
    public RefundResponse createRefund(CallerContext callerContext, RefundCreateRequest request) {
        Long companyId = callerContext.companyId();
        Long userId = callerContext.userId();

        if (companyId == null) {
            throw new IllegalArgumentException("companyId is required");
        }
        if (request.getBookingType() == null) {
            throw new IllegalArgumentException("bookingType is required");
        }
        if (request.getRequestedAmount() == null
            || request.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("requestedAmount must be greater than zero");
        }

        boolean hasFlight = request.getBookingFlightId() != null;
        boolean hasHotel = request.getBookingHotelId() != null;
        if (hasFlight == hasHotel) {
            throw new IllegalArgumentException(
                "exactly one of bookingFlightId or bookingHotelId must be provided");
        }
        if (request.getBookingType() == BookingType.FLIGHT && !hasFlight) {
            throw new IllegalArgumentException("bookingFlightId is required when bookingType is FLIGHT");
        }
        if (request.getBookingType() == BookingType.HOTEL && !hasHotel) {
            throw new IllegalArgumentException("bookingHotelId is required when bookingType is HOTEL");
        }

        String bookingCode;
        BookingPaymentMethod paymentMethod;
        Long bookingId;
        String otaReference;
        String clientSource;

        if (request.getBookingType() == BookingType.FLIGHT) {
            BookingFlight flight = bookingFlightRepository
                .findByIdInAndCompanyId(java.util.List.of(request.getBookingFlightId()), companyId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Booking flight not found for this company"));

            if (flight.getRefundId() != null) {
                throw new IllegalArgumentException("Booking already has a refund");
            }
            if (flight.getInvoiceId() != null) {
                throw new IllegalArgumentException("Cannot refund an invoiced booking");
            }
            if (flight.getStatus() != BookingFlightStatus.ISSUED) {
                throw new IllegalArgumentException("Only ISSUED booking can be refunded");
            }

            bookingCode = flight.getBookingCode();
            paymentMethod = flight.getPaymentMethod();
            bookingId = flight.getId();
            otaReference = flight.getOtaReference();
            clientSource = flight.getClientSource();
        } else {
            BookingHotel hotel = bookingHotelRepository
                .findByIdInAndCompanyId(java.util.List.of(request.getBookingHotelId()), companyId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Booking hotel not found for this company"));

            if (hotel.getRefundId() != null) {
                throw new IllegalArgumentException("Booking already has a refund");
            }
            if (hotel.getInvoiceId() != null) {
                throw new IllegalArgumentException("Cannot refund an invoiced booking");
            }
            if (hotel.getStatus() != BookingHotelStatus.ISSUED) {
                throw new IllegalArgumentException("Only ISSUED booking can be refunded");
            }

            bookingCode = hotel.getBookingCode();
            paymentMethod = hotel.getPaymentMethod();
            bookingId = hotel.getId();
            otaReference = hotel.getOtaReference();
            clientSource = hotel.getClientSource();
        }

        if (otaReference == null || otaReference.isBlank()) {
            throw new IllegalArgumentException(
                "Booking has no provider reference; cannot submit refund to provider");
        }

        if (refundRepository.existsByCompanyIdAndBookingCodeAndStatusNot(
            companyId, bookingCode, RefundStatus.CANCELLED)) {
            throw new IllegalArgumentException("Booking already has an active refund");
        }

        String code = request.getCode();
        if (code != null && !code.isBlank()) {
            if (refundRepository.existsByCompanyIdAndCode(companyId, code)) {
                throw new IllegalArgumentException("Refund code already exists");
            }
        }

        Refund refund = Refund.builder()
            .companyId(companyId)
            .bookingType(request.getBookingType())
            .bookingCode(bookingCode)
            .paymentMethod(paymentMethod)
            .requestedAmount(request.getRequestedAmount())
            .currency("IDR")
            .status(RefundStatus.CREATED)
            .remarks(request.getRemarks())
            .createdBy(userId)
            .updatedBy(userId)
            .build();

        if (code == null || code.isBlank()) {
            String prefix = request.getBookingType() == BookingType.FLIGHT ? "RFDF-" : "RFDH-";
            code = prefix + Instant.now().toEpochMilli();
            refund.setCode(code);
        } else {
            refund.setCode(code);
        }
        refundRepository.save(refund);

        int linked = request.getBookingType() == BookingType.FLIGHT
            ? bookingFlightRepository.linkRefund(bookingId, companyId, refund.getId(), userId)
            : bookingHotelRepository.linkRefund(bookingId, companyId, refund.getId(), userId);

        if (linked != 1) {
            throw new IllegalArgumentException("Booking already linked to another refund");
        }

        RefundProviderRequest providerRequest = RefundProviderRequest.builder()
            .partnerBookingId(bookingCode)
            .bookingId(otaReference)
            .cancellationReason(request.getRemarks())
            .build();

        if (request.getBookingType() == BookingType.FLIGHT) {
            getFlightProvider(clientSource).submitRefund(callerContext, providerRequest);
        } else {
            getHotelProvider(clientSource).submitCancellation(callerContext, providerRequest);
        }

        return toResponse(refund);
    }

    @Transactional
    public RefundResponse paidRefund(CallerContext callerContext, RefundPaidRequest request) {
        Long userId = callerContext.userId();

        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }
        if (request.getType() == null) {
            throw new IllegalArgumentException("type is required");
        }
        if (request.getPartnerBookingId() == null || request.getPartnerBookingId().isBlank()) {
            throw new IllegalArgumentException("partnerBookingId is required");
        }
        if (request.getBookingId() == null || request.getBookingId().isBlank()) {
            throw new IllegalArgumentException("bookingId is required");
        }
        if (request.getPaidAmount() == null
            || request.getPaidAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("paidAmount must be greater than zero");
        }

        Refund refund = findRefundByBooking(
            request.getType(), request.getPartnerBookingId(), request.getBookingId());

        if (refund.getStatus() == RefundStatus.PAID) {
            return toResponse(refund);
        }
        if (refund.getStatus() == RefundStatus.CANCELLED) {
            throw new IllegalArgumentException("cannot pay a cancelled refund");
        }

        refund.setPaidAmount(request.getPaidAmount());
        refund.setPaidAt(LocalDateTime.now());
        refund.setStatus(RefundStatus.PAID);
        if (request.getRemarks() != null && !request.getRemarks().isBlank()) {
            refund.setRemarks(request.getRemarks());
        }
        refund.setUpdatedBy(userId);
        refundRepository.save(refund);

        adjustFunds(callerContext, refund);
        markBookingRefunded(refund, userId);

        return toResponse(refund);
    }

    @Transactional
    public RefundResponse cancelRefund(CallerContext callerContext, RefundCancelRequest request) {
        Long userId = callerContext.userId();

        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }
        if (request.getType() == null) {
            throw new IllegalArgumentException("type is required");
        }
        if (request.getPartnerBookingId() == null || request.getPartnerBookingId().isBlank()) {
            throw new IllegalArgumentException("partnerBookingId is required");
        }
        if (request.getBookingId() == null || request.getBookingId().isBlank()) {
            throw new IllegalArgumentException("bookingId is required");
        }

        Refund refund = findRefundByBooking(
            request.getType(), request.getPartnerBookingId(), request.getBookingId());

        if (refund.getStatus() == RefundStatus.CANCELLED) {
            return toResponse(refund);
        }
        if (refund.getStatus() == RefundStatus.PAID) {
            throw new IllegalArgumentException("cannot cancel a paid refund");
        }

        refund.setStatus(RefundStatus.CANCELLED);
        refund.setCancelledAt(LocalDateTime.now());
        if (request.getRemarks() != null && !request.getRemarks().isBlank()) {
            refund.setRemarks(request.getRemarks());
        }
        refund.setUpdatedBy(userId);
        refundRepository.save(refund);

        if (refund.getBookingType() == BookingType.FLIGHT) {
            bookingFlightRepository.unlinkRefund(refund.getId(), userId);
        } else if (refund.getBookingType() == BookingType.HOTEL) {
            bookingHotelRepository.unlinkRefund(refund.getId(), userId);
        }

        return toResponse(refund);
    }

    @Transactional(readOnly = true)
    public RefundResponse getById(CallerContext callerContext, Long id) {
        Refund refund = refundRepository.findByIdAndCompanyId(id, callerContext.companyId())
            .orElseThrow(() -> new NotFoundException("Refund not found"));
        return toResponse(refund);
    }

    @Transactional(readOnly = true)
    public Page<RefundResponse> getList(CallerContext callerContext, RefundFilter filter, Pageable pageable) {
        filter.setCompanyId(callerContext.companyId());
        Page<Refund> page = refundRepository.findAll(RefundSpecification.filter(filter), pageable);
        return page.map(this::toResponse);
    }

    private Refund findRefundByBooking(BookingType type, String bookingCode, String otaReference) {
        Long refundId;
        if (type == BookingType.FLIGHT) {
            BookingFlight flight = bookingFlightRepository
                .findByBookingCodeOrderByIdAsc(bookingCode)
                .stream()
                .filter(b -> otaReference.equals(b.getOtaReference()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                    "Booking flight not found for the given partnerBookingId and bookingId"));
            refundId = flight.getRefundId();
        } else if (type == BookingType.HOTEL) {
            BookingHotel hotel = bookingHotelRepository
                .findByBookingCode(bookingCode)
                .stream()
                .filter(b -> otaReference.equals(b.getOtaReference()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                    "Booking hotel not found for the given partnerBookingId and bookingId"));
            refundId = hotel.getRefundId();
        } else {
            throw new IllegalArgumentException("Unsupported booking type: " + type);
        }

        if (refundId == null) {
            throw new NotFoundException("Booking has no refund");
        }
        return refundRepository.findById(refundId)
            .orElseThrow(() -> new NotFoundException("Refund not found"));
    }

    private void adjustFunds(CallerContext callerContext, Refund refund) {
        BookingPaymentMethod method = refund.getPaymentMethod();
        if (method == null) {
            return;
        }

        switch (method) {
            case DEPOSIT -> balanceService.adjustBalance(
                callerContext,
                BalanceAdjustRequest.builder()
                    .companyId(refund.getCompanyId())
                    .code(refund.getBookingType() == BookingType.FLIGHT
                        ? BalanceCodeType.WALLET_FLIGHT
                        : BalanceCodeType.WALLET_HOTEL)
                    .sourceType(BalanceSourceType.BOOKING_REFUND)
                    .bookingType(refund.getBookingType())
                    .type(BalanceTransactionType.CREDIT)
                    .amount(refund.getPaidAmount())
                    .referenceId(refund.getId())
                    .referenceCode(refund.getCode())
                    .remarks("Refund paid")
                    .build()
            );
            case LIMIT -> creditService.adjustBalance(
                callerContext,
                CreditAdjustRequest.builder()
                    .companyId(refund.getCompanyId())
                    .code(refund.getBookingType() == BookingType.FLIGHT
                        ? CreditCodeType.CREDIT_FLIGHT
                        : CreditCodeType.CREDIT_HOTEL)
                    .sourceType(CreditSourceType.BOOKING_REFUND)
                    .bookingType(refund.getBookingType())
                    .type(CreditTransactionType.CREDIT)
                    .amount(refund.getPaidAmount())
                    .referenceId(refund.getId())
                    .referenceCode(refund.getCode())
                    .remarks("Refund paid")
                    .build()
            );
            case CUST_CREDIT_CARD -> {
                // No internal balance impact
            }
        }
    }

    private void markBookingRefunded(Refund refund, Long userId) {
        if (refund.getBookingType() == BookingType.FLIGHT) {
            bookingFlightRepository.findFirstByRefundId(refund.getId())
                .ifPresent(f -> bookingFlightRepository.updateStatusById(
                    f.getId(), BookingFlightStatus.REFUNDED, userId));
        } else if (refund.getBookingType() == BookingType.HOTEL) {
            bookingHotelRepository.findFirstByRefundId(refund.getId())
                .ifPresent(h -> bookingHotelRepository.updateStatusById(
                    h.getId(), BookingHotelStatus.REFUNDED, userId));
        }
    }

    private RefundResponse toResponse(Refund r) {
        return RefundResponse.builder()
            .id(r.getId())
            .companyId(r.getCompanyId())
            .code(r.getCode())
            .bookingType(r.getBookingType())
            .bookingCode(r.getBookingCode())
            .paymentMethod(r.getPaymentMethod())
            .requestedAmount(r.getRequestedAmount())
            .paidAmount(r.getPaidAmount())
            .currency(r.getCurrency())
            .status(r.getStatus())
            .remarks(r.getRemarks())
            .paidAt(r.getPaidAt())
            .cancelledAt(r.getCancelledAt())
            .createdAt(r.getCreatedAt())
            .updatedAt(r.getUpdatedAt())
            .build();
    }
}
