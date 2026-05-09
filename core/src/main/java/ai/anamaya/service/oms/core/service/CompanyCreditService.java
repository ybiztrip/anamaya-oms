package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.CompanyCreditInvoiceListFilter;
import ai.anamaya.service.oms.core.dto.request.CompanyCreditInvoiceRequest;
import ai.anamaya.service.oms.core.dto.request.CreditAdjustRequest;
import ai.anamaya.service.oms.core.dto.response.CompanyCreditInvoiceResponse;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.entity.CompanyCreditInvoice;
import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
import ai.anamaya.service.oms.core.enums.CreditCodeType;
import ai.anamaya.service.oms.core.enums.CreditSourceType;
import ai.anamaya.service.oms.core.enums.CreditTransactionType;
import ai.anamaya.service.oms.core.enums.InvoiceProductType;
import ai.anamaya.service.oms.core.enums.InvoiceStatus;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.BookingFlightRepository;
import ai.anamaya.service.oms.core.repository.BookingHotelRepository;
import ai.anamaya.service.oms.core.repository.CompanyCreditInvoiceRepository;
import ai.anamaya.service.oms.core.specification.CompanyCreditInvoiceSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyCreditService {

    private  final CreditService creditService;
    private final CompanyCreditInvoiceRepository companyCreditInvoiceRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final BookingHotelRepository bookingHotelRepository;

    public Page<CompanyCreditInvoiceResponse> getAll(CallerContext callerContext, CompanyCreditInvoiceListFilter filter) {

        // Sorting
        Sort sorting = Sort.by("createdAt").descending();

        if (filter.getSort() != null && !filter.getSort().isBlank()) {
            String[] parts = filter.getSort().split(";");
            String field = parts[0];

            Sort.Direction direction =
                (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            sorting = Sort.by(direction, field);
        }

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sorting);

        Specification<CompanyCreditInvoice> spec = CompanyCreditInvoiceSpecification.filter(filter);

        Page<CompanyCreditInvoice> companyCreditInvoices = companyCreditInvoiceRepository.findAll(spec, pageable);

        List<CompanyCreditInvoiceResponse> mapped = companyCreditInvoices.getContent().stream()
            .map(this::toResponse)
            .toList();

        return new PageImpl<>(mapped, pageable, companyCreditInvoices.getTotalElements());
    }

    @Transactional
    public CompanyCreditInvoiceResponse createInvoice(CallerContext callerContext, CompanyCreditInvoiceRequest request) {
        Long companyId = request.getCompanyId();
        Long userId = callerContext.userId();

        if (companyId == null || companyId == 0) {
            throw new IllegalArgumentException("companyId is required");
        }
        if (request.getDocNo() == null || request.getDocNo().isBlank()) {
            throw new IllegalArgumentException("docNo is required");
        }
        if (request.getType() == null) {
            throw new IllegalArgumentException("type is required");
        }

        boolean hasFlightIds = request.getBookingFlightIds() != null && !request.getBookingFlightIds().isEmpty();
        boolean hasHotelIds = request.getBookingHotelIds() != null && !request.getBookingHotelIds().isEmpty();
        if (hasFlightIds == hasHotelIds) {
            throw new IllegalArgumentException("exactly one of bookingFlightIds or bookingHotelIds must be provided");
        }
        if (request.getType() == InvoiceProductType.FLIGHT && !hasFlightIds) {
            throw new IllegalArgumentException("bookingFlightIds is required when productType is FLIGHT");
        }
        if (request.getType() == InvoiceProductType.HOTEL && !hasHotelIds) {
            throw new IllegalArgumentException("bookingHotelIds is required when productType is HOTEL");
        }

        if (companyCreditInvoiceRepository.existsByCompanyIdAndDocNo(companyId, request.getDocNo())) {
            throw new IllegalArgumentException("Doc no already exists");
        }

        BigDecimal total;
        CreditCodeType code;
        int expected;
        List<Long> ids;

        if (request.getType() == InvoiceProductType.FLIGHT) {
            ids = request.getBookingFlightIds();
            List<BookingFlight> flights = bookingFlightRepository.findByIdInAndCompanyId(ids, companyId);
            validateFlights(flights, ids);
            total = sumFlightAmounts(flights);
            code = CreditCodeType.CREDIT_FLIGHT;
            expected = ids.size();
        } else {
            ids = request.getBookingHotelIds();
            List<BookingHotel> hotels = bookingHotelRepository.findByIdInAndCompanyId(ids, companyId);
            validateHotels(hotels, ids);
            total = sumHotelAmounts(hotels);
            code = CreditCodeType.CREDIT_HOTEL;
            expected = ids.size();
        }

        CompanyCreditInvoice invoice = CompanyCreditInvoice.builder()
            .companyId(companyId)
            .code(code)
            .docNo(request.getDocNo())
            .amount(total)
            .currency("IDR")
            .status(InvoiceStatus.CREATED)
            .createdBy(userId)
            .updatedBy(userId)
            .build();
        companyCreditInvoiceRepository.save(invoice);

        int linked = (request.getType() == InvoiceProductType.FLIGHT)
            ? bookingFlightRepository.linkInvoice(ids, companyId, invoice.getId(), userId)
            : bookingHotelRepository.linkInvoice(ids, companyId, invoice.getId(), userId);

        if (linked != expected) {
            throw new IllegalArgumentException("one or more bookings already linked to another invoice");
        }

        return toResponse(invoice);
    }

    private void validateFlights(List<BookingFlight> flights, List<Long> requestedIds) {
        if (flights.size() != requestedIds.size()) {
            throw new NotFoundException("one or more bookingFlightIds not found for this company");
        }
        for (BookingFlight f : flights) {
            if (f.getPaymentMethod() != BookingPaymentMethod.LIMIT) {
                throw new IllegalArgumentException("bookingFlightId " + f.getId() + " is not paid by LIMIT");
            }
            if (f.getInvoiceId() != null) {
                throw new IllegalArgumentException("bookingFlightId " + f.getId() + " is already invoiced");
            }
            if (f.getStatus() != BookingFlightStatus.PAID && f.getStatus() != BookingFlightStatus.ISSUED) {
                throw new IllegalArgumentException("bookingFlightId " + f.getId() + " status is not billable");
            }
        }
    }

    private void validateHotels(List<BookingHotel> hotels, List<Long> requestedIds) {
        if (hotels.size() != requestedIds.size()) {
            throw new NotFoundException("one or more bookingHotelIds not found for this company");
        }
        for (BookingHotel h : hotels) {
            if (h.getPaymentMethod() != BookingPaymentMethod.LIMIT) {
                throw new IllegalArgumentException("bookingHotelId " + h.getId() + " is not paid by LIMIT");
            }
            if (h.getInvoiceId() != null) {
                throw new IllegalArgumentException("bookingHotelId " + h.getId() + " is already invoiced");
            }
            if (h.getStatus() != BookingHotelStatus.PAID && h.getStatus() != BookingHotelStatus.ISSUED) {
                throw new IllegalArgumentException("bookingHotelId " + h.getId() + " status is not billable");
            }
        }
    }

    private BigDecimal sumFlightAmounts(List<BookingFlight> flights) {
        BigDecimal total = BigDecimal.ZERO;
        for (BookingFlight f : flights) {
            BigDecimal base = f.getTotalAmount() != null ? f.getTotalAmount() : BigDecimal.ZERO;
            BigDecimal fee = f.getManagementFeeAmount() != null ? f.getManagementFeeAmount() : BigDecimal.ZERO;
            total = total.add(base).add(fee);
        }
        return total;
    }

    private BigDecimal sumHotelAmounts(List<BookingHotel> hotels) {
        BigDecimal total = BigDecimal.ZERO;
        for (BookingHotel h : hotels) {
            BigDecimal base = h.getPartnerSellAmount() != null
                ? BigDecimal.valueOf(h.getPartnerSellAmount())
                : BigDecimal.ZERO;
            BigDecimal fee = h.getManagementFeeAmount() != null ? h.getManagementFeeAmount() : BigDecimal.ZERO;
            total = total.add(base).add(fee);
        }
        return total;
    }

    @Transactional
    public CompanyCreditInvoiceResponse paidInvoice(CallerContext callerContext, Long id) {
        Optional<CompanyCreditInvoice> data = companyCreditInvoiceRepository.findById(id);
        if(data.isEmpty()) {
            throw new NotFoundException("Data not found");
        }

        CompanyCreditInvoice companyCreditInvoice = data.get();
        if(companyCreditInvoice.getStatus() == InvoiceStatus.PAID) {
            return toResponse(companyCreditInvoice);
        }

        creditService.adjustBalance(
            callerContext,
            CreditAdjustRequest.builder()
                .companyId(companyCreditInvoice.getCompanyId())
                .code(companyCreditInvoice.getCode())
                .sourceType(CreditSourceType.INVOICE)
                .type(CreditTransactionType.CREDIT)
                .amount(companyCreditInvoice.getAmount())
                .referenceId(companyCreditInvoice.getId())
                .referenceCode(companyCreditInvoice.getDocNo())
                .remarks("Invoice paid")
                .build());

        companyCreditInvoice.setStatus(InvoiceStatus.PAID);
        companyCreditInvoiceRepository.save(companyCreditInvoice);

        return toResponse(companyCreditInvoice);
    }

    private CompanyCreditInvoiceResponse toResponse(CompanyCreditInvoice c) {
        return CompanyCreditInvoiceResponse.builder()
            .id(c.getId())
            .companyId(c.getCompanyId())
            .code(c.getCode())
            .docNo(c.getDocNo())
            .amount(c.getAmount())
            .currency(c.getCurrency())
            .status(c.getStatus())
            .createdAt(c.getCreatedAt())
            .updatedAt(c.getUpdatedAt())
            .build();
    }
}
