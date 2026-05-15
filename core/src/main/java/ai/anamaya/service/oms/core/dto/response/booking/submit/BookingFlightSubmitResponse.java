package ai.anamaya.service.oms.core.dto.response.booking.submit;

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
        private List<PnrData> departurePnr;
        private List<PnrData> returnPnr;
    }

    @Data
    public static class PnrData {
        private String providerPnr;
        private List<AirlinePnrItems> airlinePnrItems;
    }

    @Data
    public static class AirlinePnrItems {
        private String airlinePnr;
        private String segment;
    }
}
