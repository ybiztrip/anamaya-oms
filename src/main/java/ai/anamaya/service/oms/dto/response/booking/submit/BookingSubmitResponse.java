package ai.anamaya.service.oms.dto.response.booking.submit;

import lombok.Data;

@Data
public class BookingSubmitResponse {
    private String bookingSubmissionStatus;
    private String bookingId;
    private String partnerBookingId;
    private Long paymentExpirationTime;
    private FlightBookingDetail flightBookingDetail;
}
