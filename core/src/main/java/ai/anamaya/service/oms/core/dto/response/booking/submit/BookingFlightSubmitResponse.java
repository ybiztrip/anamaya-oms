package ai.anamaya.service.oms.core.dto.response.booking.submit;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.BiztripSubmitResponse;
import lombok.Data;

import java.util.List;

@Data
public class BookingFlightSubmitResponse {
    private boolean isError;
    private String errorMessage;
    private String bookingSubmissionStatus;
    private String paymentUrl;
    private String bookingId;
    private String partnerBookingId;
    private PnrInfo pnrInfo;
    private Long paymentExpirationTime;
    private FlightBookingDetail flightBookingDetail;

    @Data
    public static class PnrInfo {
        private List<BiztripSubmitResponse.PnrData> departurePnr;
        private List<BiztripSubmitResponse.PnrData> returnPnr;
    }

    @Data
    public static class PnrData {
        private String providerPnr;
    }
}
