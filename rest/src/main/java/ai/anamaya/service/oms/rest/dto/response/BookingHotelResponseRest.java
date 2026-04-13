package ai.anamaya.service.oms.rest.dto.response;

import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingHotelResponseRest {
    private Long id;
    private Long companyId;
    private Long bookingId;
    private String bookingCode;
    private String clientSource;
    private String itemId;
    private String rateKey;
    private Short numRoom;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Double partnerSellAmount;
    private Double partnerNettAmount;
    private String currency;
    private String specialRequest;
    private String paymentUrl;
    private BookingHotelStatus status;
    private JsonNode metadata;
    private LocalDateTime createdAt;

    private List<BookingPaxResponseRest> paxs;
}
