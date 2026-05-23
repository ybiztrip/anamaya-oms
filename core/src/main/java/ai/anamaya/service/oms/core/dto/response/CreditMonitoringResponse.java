package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.BookingType;
import ai.anamaya.service.oms.core.enums.CreditCodeType;
import ai.anamaya.service.oms.core.enums.CreditSourceType;
import ai.anamaya.service.oms.core.enums.CreditTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditMonitoringResponse {
    private Long id;
    private Long companyId;
    private CreditCodeType creditCode;
    private String referenceCode;
    private Long referenceId;
    private CreditSourceType sourceType;
    private BookingType bookingType;
    private CreditTransactionType type;
    private BigDecimal amount;
    private BigDecimal beginBalance;
    private BigDecimal endBalance;
    private String remarks;
    private LocalDateTime createdAt;

    private List<BookingFlightResponse> bookingFlights;
    private List<BookingHotelResponse> bookingHotels;
}
