package ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response;

import lombok.Data;

import java.util.List;

@Data
public class BiztripHotelBookingDetailResponse {

    private String bookingId;
    private String partnerBookingId;
    private String bookingStatus;
    private String propertyId;
    private String checkInDate;
    private String checkOutDate;
    private List<Room> rooms;

    @Data
    public static class Room {
        private TotalRates totalSettlementRates;
    }

    @Data
    public static class TotalRates {
        private String displayCurrency;
        private String displayAmount;
        private String partnerCurrency;
        private String partnerAmount;
    }
}

