package ai.anamaya.service.oms.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingHotelResponseRest {
    private Long id;
    private Long bookingId;
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
    private Short status;
}
