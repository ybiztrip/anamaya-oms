package ai.anamaya.service.oms.core.dto.response.booking.hotel;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class HotelBookingCheckRateResponse {

    private Boolean isCancel;
    private String paymentKey;
    private String rateStatus;
    private String roomName;
    private String roomType;
    private String currency;
    private Long sellAmount;
    private Long nettAmount;
    private Boolean refundable;
    private List<Charge> charges;

    @Data
    @Builder
    public static class Charge {
        private String type;
        private Long amount;
        private Boolean included;
    }
}
