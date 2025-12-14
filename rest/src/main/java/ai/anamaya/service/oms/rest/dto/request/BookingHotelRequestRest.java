package ai.anamaya.service.oms.rest.dto.request;

import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
import lombok.Data;

@Data
public class BookingHotelRequestRest {
    private Long id;
    private boolean deleted;

    private String clientSource;
    private String itemId;
    private String roomId;
    private String rateKey;
    private Integer numRoom;
    private String checkInDate;
    private String checkOutDate;
    private String partnerSellAmount;
    private String partnerNettAmount;
    private String currency;
    private String specialRequest;
    private BookingHotelStatus status;
}
