package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

import java.util.List;

@Data
public class BiztripCheckFullStatusResult {
    private String status;
    private String bookingId;
    private PnrInfo pnrInfo;

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
