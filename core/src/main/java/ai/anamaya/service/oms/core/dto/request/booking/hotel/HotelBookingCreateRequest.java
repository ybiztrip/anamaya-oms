package ai.anamaya.service.oms.core.dto.request.booking.hotel;

import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelBookingCreateRequest {

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

    private BookingPaymentMethod paymentMethod;
    private String paymentReference1;
    private String paymentReference2;

    /**
     * JSON String (kept as string to stay provider-agnostic)
     */
    private String additionalData;

    // ============================
    // Nested classes
    // ============================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Room {
        private String roomId;
        private String rateKey;
        private String paymentKey;
        private Integer numRooms;
        private Integer numAdults;
        private Integer numChild;
        private List<Integer> childrenAges;
        private List<GuestInfo> guestInfo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
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
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TotalRates {
        private String partnerSellAmount;
        private String partnerNettAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerInfo {
        private String title;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserPayment {
        private BookingPaymentMethod userPayment;
        private CreditCardDetail creditCardDetail;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreditCardDetail {
        private String lastSixDigitNumber;
        private String cardName;
    }
}
