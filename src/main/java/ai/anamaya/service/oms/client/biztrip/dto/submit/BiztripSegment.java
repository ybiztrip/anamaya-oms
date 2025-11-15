package ai.anamaya.service.oms.client.biztrip.dto.submit;

import lombok.Data;

@Data
public class BiztripSegment {
    private String flightCode;
    private String marketingAirline;
    private String brandAirline;
    private String operatingAirline;
    private String subClass;
    private String seatClass;
    private String flightDurationInMinutes;
    private String transitDurationInMinutes;
    private BiztripSegmentDetail departureDetail;
    private BiztripSegmentDetail arrivalDetail;
    private Object stopInfo;
    private Object addOns;
    private String fareBasisCode;
    private Boolean visaRequired;
    private Boolean mayNeedReCheckIn;
}
