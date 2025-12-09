package ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request;

import lombok.Data;
import java.util.List;

@Data
public class BiztripHotelBookingCreateRequest {

    private String propertyId;
    private String partnerBookingId;
    private List<Room> rooms;
    private String checkInDate;
    private String checkOutDate;
    private TotalRates totalRates;
    private CustomerInfo customerInfo;
    private String language;
    private String userNationality;
    private UserPayment userPayment;
    private String specialRequest;
    private String displayCurrency;
    private String additionalData;

    @Data
    public static class Room {
        private String roomId;
        private String rateKey;
        private Integer numRooms;
        private Integer numAdults;
        private Integer numChild;
        private List<Integer> childrenAges;
        private List<GuestInfo> guestInfo;
    }

    @Data
    public static class GuestInfo {
        private String firstName;
        private String lastName;
        private String idtype;
        private String gender;
        private String idnumber;
        private String title;
        private String email;
    }

    @Data
    public static class TotalRates {
        private String partnerSellAmount;
        private String partnerNettAmount;
    }

    @Data
    public static class CustomerInfo {
        private String title;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
    }

    @Data
    public static class UserPayment {
        private String userPayment;
    }
}
