package ai.anamaya.service.oms.core.client.biztrip.mapper.request;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request.BiztripHotelGetGeoListRequest;
import ai.anamaya.service.oms.core.dto.request.HotelGeoListRequest;


public class BiztripHotelGeoListRequestMapper {

    public BiztripHotelGetGeoListRequest map(HotelGeoListRequest request) {
        BiztripHotelGetGeoListRequest req =
            new BiztripHotelGetGeoListRequest();
        req.setCountyCode(request.getCountryCode());
        req.setKey(request.getKey());
        req.setOffset(request.getOffset());
        req.setLimit(request.getLimit());

        return req;
    }
}
