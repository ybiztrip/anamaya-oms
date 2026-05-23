package ai.anamaya.service.oms.rest.dto.response;

import ai.anamaya.service.oms.core.enums.BookingType;
import ai.anamaya.service.oms.core.enums.CreditCodeType;
import ai.anamaya.service.oms.core.enums.CreditSourceType;
import ai.anamaya.service.oms.core.enums.CreditTransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditMonitoringResponseRest {
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

    private List<BookingFlightResponseRest> bookingFlights;
    private List<BookingHotelResponseRest> bookingHotels;
}
