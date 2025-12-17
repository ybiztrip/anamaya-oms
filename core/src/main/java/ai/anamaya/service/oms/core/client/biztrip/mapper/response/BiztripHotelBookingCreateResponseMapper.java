package ai.anamaya.service.oms.core.client.biztrip.mapper.response;


import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelBookingCreateResponse;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCreateResponse;

import java.math.BigDecimal;

public class BiztripHotelBookingCreateResponseMapper {

    public HotelBookingCreateResponse map(Boolean success, BiztripHotelBookingCreateResponse source) {
        if (!success) {
            return HotelBookingCreateResponse.builder()
                .isCancel(true)
                .build();
        }

        return HotelBookingCreateResponse.builder()
            .bookingReference(source.getBookingId())
            .status(source.getBookingStatus())
            .currency(source.getTotalChargeableRate().getCurrencyCode())
            .totalAmount(
                new BigDecimal(source.getTotalChargeableRate().getAmount()).longValue()
            )
            .build();
    }
}
