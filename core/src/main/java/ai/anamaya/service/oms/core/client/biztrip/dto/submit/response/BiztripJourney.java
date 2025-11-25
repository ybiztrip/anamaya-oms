package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

import java.util.List;

@Data
public class BiztripJourney {
    private String numOfTransits;
    private String journeyDuration;
    private String daysOffset;
    private String refundableStatus;
    private BiztripSegmentDetail departureDetail;
    private BiztripSegmentDetail arrivalDetail;
    private BiztripPartnerFareInfo fareInfo;
    private List<BiztripSegment> segments;
}
