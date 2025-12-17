package ai.anamaya.service.oms.core.client.biztrip.mapper.response;


import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelRateCheckResponse;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCheckRateResponse;

import java.util.stream.Collectors;

public class BiztripHotelBookingCheckRateResponseMapper {

    public HotelBookingCheckRateResponse map(Boolean success, BiztripHotelRateCheckResponse source) {
       if(!success) {
           return HotelBookingCheckRateResponse.builder()
               .isCancel(true)
               .build();
       }

        return HotelBookingCheckRateResponse.builder()
            .isCancel(!success)
            .paymentKey(source.getRateKey())
            .rateStatus(source.getRateStatus())
            .roomName(source.getRoomName())
            .roomType(source.getRoomType())
            .currency(source.getTotalRates().getDisplayCurrency())
            .sellAmount(source.getTotalRates().getDisplaySellAmount())
            .nettAmount(source.getTotalRates().getDisplayNettAmount())
            .refundable(source.getRefundable())
            .charges(
                source.getCharges() == null ? null :
                    source.getCharges().stream()
                        .map(c -> HotelBookingCheckRateResponse.Charge.builder()
                            .type(c.getType())
                            .amount(c.getDisplayAmount())
                            .included(c.getIncluded())
                            .build())
                        .collect(Collectors.toList())
            )
            .build();
    }
}
