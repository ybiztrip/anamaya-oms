package ai.anamaya.service.oms.core.client.biztrip.mapper.request;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request.BiztripHotelBookingDetailRequest;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingGetDetailRequest;

public class BiztripHotelBookingDetailRequestMapper {

    public BiztripHotelBookingDetailRequest map(HotelBookingGetDetailRequest request) {

        BiztripHotelBookingDetailRequest target =
            new BiztripHotelBookingDetailRequest();
        target.setBookingId(request.getBookingId());
        target.setPartnerBookingId(request.getPartnerBookingId());

        return target;
    }
}
