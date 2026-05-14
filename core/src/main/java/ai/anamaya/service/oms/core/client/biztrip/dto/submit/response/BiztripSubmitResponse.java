package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

import java.util.List;

@Data
public class BiztripSubmitResponse {
    private String bookingSubmissionStatus;
    private String bookingId;
    private String partnerBookingId;
    private PnrInfo pnrInfo;
    private Long paymentExpirationTime;
    private BiztripFlightBookingDetail flightBookingDetail;

    @Data
    public static class PnrInfo {
        private List<PnrData> departurePnr;
        private List<PnrData> returnPnr;
    }

    @Data
    public static class PnrData {
        private String providerPnr;
    }

}
