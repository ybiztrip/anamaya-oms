package ai.anamaya.service.oms.dto.request;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingHotelRequest {
    private Long id;
    private boolean deleted;

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
