package ai.anamaya.service.oms.core.client.biztrip.mapper.request;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request.BiztripHotelBookingCheckRateRequest;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingCheckRateRequest;

public class BiztripHotelBookingCheckRateRequestMapper {

    public BiztripHotelBookingCheckRateRequest map(HotelBookingCheckRateRequest source) {

        if (source == null) {
            return null;
        }

        BiztripHotelBookingCheckRateRequest target =
            new BiztripHotelBookingCheckRateRequest();

        target.setPropertyId(source.getPropertyId());
        target.setRoomId(source.getRoomId());
        target.setCheckInDate(source.getCheckInDate());
        target.setCheckOutDate(source.getCheckOutDate());
        target.setNumRooms(source.getNumRooms());
        target.setNumAdults(source.getNumAdults());
        target.setLanguage(source.getLanguage());
        target.setDisplayCurrency(source.getDisplayCurrency());
        target.setUserNationality(source.getUserNationality());
        target.setRateKey(source.getRateKey());

        return target;
    }
}
