package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingHotelResponse {
    private Long id;
    private Long companyId;
    private Long bookingId;
    private String bookingCode;
    private String clientSource;
    private String itemId;
    private String roomId;
    private String rateKey;
    private Short numRoom;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Double partnerSellAmount;
    private Double partnerNettAmount;
    private String currency;
    private String specialRequest;
    private String paymentUrl;
    private BigDecimal managementFeeAmount;
    private BookingPaymentMethod paymentMethod;
    private Long invoiceId;
    private BookingHotelStatus status;
    private JsonNode metadata;
    private String errorMessage;
    private LocalDateTime createdAt;

    private List<BookingPaxResponse> paxs;
}
