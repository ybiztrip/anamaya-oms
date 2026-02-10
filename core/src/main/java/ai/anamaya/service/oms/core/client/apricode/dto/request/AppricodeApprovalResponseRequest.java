package ai.anamaya.service.oms.core.client.apricode.dto.request;

import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppricodeApprovalResponseRequest {

    private Booking booking;
    private User user;

    @Data
    public static class Booking {
        private Long bookingId;
        private List<Hotel> hotels;
        private List<Flight> flights;
    }

    @Data
    public static class Hotel {
        private Long id;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private BigDecimal totalAmount;
        private BookingHotelStatus status;
    }

    @Data
    public static class Flight {
        private Long id;
        private String origin;
        private String destination;
        private BigDecimal totalAmount;
        private LocalDateTime departureDatetime;
        private LocalDateTime arrivalDatetime;
        private BookingFlightStatus status;
    }

    @Data
    public static class User {
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNo;
    }
}
