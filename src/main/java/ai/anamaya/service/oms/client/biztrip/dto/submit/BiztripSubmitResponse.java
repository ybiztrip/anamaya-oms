package ai.anamaya.service.oms.client.biztrip.dto.submit;

import lombok.Data;

@Data
public class BiztripSubmitResponse {
    private String bookingSubmissionStatus;
    private String bookingId;
    private String partnerBookingId;
    private Long paymentExpirationTime;
    private BiztripFlightBookingDetail flightBookingDetail;
}
