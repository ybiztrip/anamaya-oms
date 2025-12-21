package ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request;

import lombok.Data;

@Data
public class BiztripHotelGetGeoListRequest {

    private String countyCode;
    private String key;
    private Integer offset;
    private Integer limit;

}
