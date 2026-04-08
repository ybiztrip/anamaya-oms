package ai.anamaya.service.oms.core.client.biztrip.mapper.request;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request.BiztripHotelPropertyDetailRequest;
import ai.anamaya.service.oms.core.dto.request.HotelPropertyDetailRequest;

public class BiztripHotelPropertyDetailRequestMapper {

    public BiztripHotelPropertyDetailRequest map(HotelPropertyDetailRequest request) {
        if (request == null) {
            return null;
        }

        BiztripHotelPropertyDetailRequest.BiztripHotelPropertyDetailRequestBuilder builder =
            BiztripHotelPropertyDetailRequest.builder()
                .propertyIds(request.getPropertyIds());

        return builder.build();
    }
}