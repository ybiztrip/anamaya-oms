package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingHotelResponse {
    private Long id;
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
    private BookingHotelStatus status;
    private List<BookingPaxResponse> paxs;
}
