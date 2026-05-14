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
        private List<BiztripSubmitResponse.PnrData> departurePnr;
        private List<BiztripSubmitResponse.PnrData> returnPnr;
    }

    @Data
    public static class PnrData {
        private String providerPnr;
    }
}
