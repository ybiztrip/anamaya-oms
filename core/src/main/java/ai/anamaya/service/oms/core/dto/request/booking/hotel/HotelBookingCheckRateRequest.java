package ai.anamaya.service.oms.core.dto.request.booking.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelBookingCheckRateRequest {

    private String propertyId;
    private String roomId;
    private String checkInDate;
    private String checkOutDate;
    private Integer numRooms;
    private Integer numAdults;
    private Integer numChilds;
    private String language;
    private String displayCurrency;
    private String userNationality;
    private String rateKey;
}
