package ai.anamaya.service.oms.rest.dto.response;

import ai.anamaya.service.oms.core.enums.BalanceCodeType;
import ai.anamaya.service.oms.core.enums.BalanceSourceType;
import ai.anamaya.service.oms.core.enums.BalanceTransactionType;
import ai.anamaya.service.oms.core.enums.BookingType;
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
public class BalanceMonitoringResponseRest {
    private Long id;
    private Long companyId;
    private BalanceCodeType balanceCode;
    private String referenceCode;
    private Long referenceId;
    private BalanceSourceType sourceType;
    private BookingType bookingType;
    private BalanceTransactionType type;
    private BigDecimal amount;
    private BigDecimal beginBalance;
    private BigDecimal endBalance;
    private String remarks;
    private LocalDateTime createdAt;

    private List<BookingFlightResponseRest> bookingFlights;
    private List<BookingHotelResponseRest> bookingHotels;
}
