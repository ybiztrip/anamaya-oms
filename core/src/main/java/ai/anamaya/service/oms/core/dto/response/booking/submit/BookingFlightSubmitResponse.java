package ai.anamaya.service.oms.core.dto.response.booking.submit;

import lombok.Data;

@Data
public class BookingFlightSubmitResponse {
    private boolean isError;
    private String errorMessage;
    private String bookingSubmissionStatus;
    private String paymentUrl;
    private String bookingId;
    private String partnerBookingId;
    private Long paymentExpirationTime;
    private FlightBookingDetail flightBookingDetail;
}
