package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.dto.request.BalanceMonitoringFilter;
import ai.anamaya.service.oms.core.dto.response.BalanceMonitoringResponse;
import ai.anamaya.service.oms.core.dto.response.BookingFlightResponse;
import ai.anamaya.service.oms.core.dto.response.BookingHotelResponse;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.entity.CompanyBalance;
import ai.anamaya.service.oms.core.entity.CompanyBalanceDetail;
import ai.anamaya.service.oms.core.enums.BookingType;
import ai.anamaya.service.oms.core.helper.json.JsonHelper;
import ai.anamaya.service.oms.core.repository.BookingFlightRepository;
import ai.anamaya.service.oms.core.repository.BookingHotelRepository;
import ai.anamaya.service.oms.core.repository.CompanyBalanceDetailRepository;
import ai.anamaya.service.oms.core.repository.CompanyBalanceRepository;
import ai.anamaya.service.oms.core.specification.CompanyBalanceDetailSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BalanceMonitoringService {

    private final CompanyBalanceRepository balanceRepository;
    private final CompanyBalanceDetailRepository detailRepository;
    private final BookingFlightRepository bookingFlightRepository;
    private final BookingHotelRepository bookingHotelRepository;
    private final JsonHelper jsonHelper;

    @Transactional(readOnly = true)
    public Page<BalanceMonitoringResponse> getMonitoring(BalanceMonitoringFilter filter, Pageable pageable) {
        Long balanceId = null;
        if (filter.getBalanceCodeType() != null && filter.getCompanyId() != null) {
            Optional<CompanyBalance> balance = balanceRepository.findByCompanyIdAndCode(
                filter.getCompanyId(), filter.getBalanceCodeType());
            if (balance.isEmpty()) {
                return new PageImpl<>(java.util.List.of(), pageable, 0);
            }
            balanceId = balance.get().getId();
        }

        Page<CompanyBalanceDetail> page = detailRepository.findAll(
            CompanyBalanceDetailSpecification.filter(filter, balanceId), pageable);

        Set<String> flightCodes = page.getContent().stream()
            .filter(d -> d.getBookingType() == BookingType.FLIGHT)
            .map(CompanyBalanceDetail::getReferenceCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Set<String> hotelCodes = page.getContent().stream()
            .filter(d -> d.getBookingType() == BookingType.HOTEL)
            .map(CompanyBalanceDetail::getReferenceCode)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        Map<String, List<BookingFlight>> flightsByCode = flightCodes.isEmpty()
            ? Map.of()
            : bookingFlightRepository.findByBookingCodeIn(flightCodes).stream()
                .collect(Collectors.groupingBy(BookingFlight::getBookingCode));

        Map<String, List<BookingHotel>> hotelsByCode = hotelCodes.isEmpty()
            ? Map.of()
            : bookingHotelRepository.findByBookingCodeIn(hotelCodes).stream()
                .collect(Collectors.groupingBy(BookingHotel::getBookingCode));

        return page.map(d -> toResponse(d, flightsByCode, hotelsByCode));
    }

    private BalanceMonitoringResponse toResponse(
        CompanyBalanceDetail d,
        Map<String, List<BookingFlight>> flightsByCode,
        Map<String, List<BookingHotel>> hotelsByCode
    ) {
        BalanceMonitoringResponse.BalanceMonitoringResponseBuilder b = BalanceMonitoringResponse.builder()
            .id(d.getId())
            .companyId(d.getBalance() != null ? d.getBalance().getCompanyId() : null)
            .balanceCode(d.getBalance() != null ? d.getBalance().getCode() : null)
            .referenceCode(d.getReferenceCode())
            .referenceId(d.getReferenceId())
            .sourceType(d.getSourceType())
            .bookingType(d.getBookingType())
            .type(d.getType())
            .amount(d.getAmount())
            .beginBalance(d.getBeginBalance())
            .endBalance(d.getEndBalance())
            .remarks(d.getRemarks())
            .createdAt(d.getCreatedAt());

        if (d.getBookingType() == BookingType.FLIGHT && d.getReferenceCode() != null) {
            b.bookingFlights(
                flightsByCode.getOrDefault(d.getReferenceCode(), List.of()).stream()
                    .map(this::toFlightResponse)
                    .toList()
            );
        } else if (d.getBookingType() == BookingType.HOTEL && d.getReferenceCode() != null) {
            b.bookingHotels(
                hotelsByCode.getOrDefault(d.getReferenceCode(), List.of()).stream()
                    .map(this::toHotelResponse)
                    .toList()
            );
        }

        return b.build();
    }

    private BookingFlightResponse toFlightResponse(BookingFlight f) {
        return BookingFlightResponse.builder()
            .id(f.getId())
            .companyId(f.getCompanyId())
            .bookingId(f.getBookingId())
            .bookingCode(f.getBookingCode())
            .type(f.getType())
            .clientSource(f.getClientSource())
            .itemId(f.getItemId())
            .origin(f.getOrigin())
            .destination(f.getDestination())
            .departureDatetime(f.getDepartureDatetime())
            .arrivalDatetime(f.getArrivalDatetime())
            .totalAmount(f.getTotalAmount())
            .managementFeeAmount(f.getManagementFeeAmount())
            .paymentMethod(f.getPaymentMethod())
            .invoiceId(f.getInvoiceId())
            .errorMessage(f.getErrorMessage())
            .status(f.getStatus())
            .metadata(
                f.getMetadata() != null
                    ? jsonHelper.toJsonNode(f.getMetadata())
                    : jsonHelper.emptyObject()
            )
            .createdAt(f.getCreatedAt())
            .build();
    }

    private BookingHotelResponse toHotelResponse(BookingHotel h) {
        return BookingHotelResponse.builder()
            .id(h.getId())
            .companyId(h.getCompanyId())
            .bookingId(h.getBookingId())
            .bookingCode(h.getBookingCode())
            .clientSource(h.getClientSource())
            .itemId(h.getItemId())
            .roomId(h.getRoomId())
            .rateKey(h.getRateKey())
            .numRoom(h.getNumRoom())
            .checkInDate(h.getCheckInDate())
            .checkOutDate(h.getCheckOutDate())
            .partnerSellAmount(h.getPartnerSellAmount())
            .partnerNettAmount(h.getPartnerNettAmount())
            .currency(h.getCurrency())
            .specialRequest(h.getSpecialRequest())
            .managementFeeAmount(h.getManagementFeeAmount())
            .paymentMethod(h.getPaymentMethod())
            .invoiceId(h.getInvoiceId())
            .errorMessage(h.getErrorMessage())
            .status(h.getStatus())
            .metadata(
                h.getMetadata() != null
                    ? jsonHelper.toJsonNode(h.getMetadata())
                    : jsonHelper.emptyObject()
            )
            .createdAt(h.getCreatedAt())
            .build();
    }
}
