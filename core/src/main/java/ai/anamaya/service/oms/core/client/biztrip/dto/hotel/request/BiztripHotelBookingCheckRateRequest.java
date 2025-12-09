package ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request;

import lombok.Data;

@Data
public class BiztripHotelBookingCheckRateRequest {

    private String propertyId;
    private String roomId;
    private String checkInDate;
    private String checkOutDate;
    private Integer numRooms;
    private Integer numAdults;
    private String language;
    private String displayCurrency;
    private String userNationality;
    private String rateKey;
}
