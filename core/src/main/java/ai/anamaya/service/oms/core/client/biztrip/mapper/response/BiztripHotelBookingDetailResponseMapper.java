package ai.anamaya.service.oms.core.client.biztrip.mapper.response;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelBookingDetailResponse;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingDetailResponse;

import java.math.BigDecimal;

public class BiztripHotelBookingDetailResponseMapper {

    public HotelBookingDetailResponse map(Boolean success, BiztripHotelBookingDetailResponse source) {
        if (!success) {
            return HotelBookingDetailResponse.builder()
                .isCancel(true)
                .build();
        }

        BiztripHotelBookingDetailResponse.Room room = source.getRooms().get(0);
        return HotelBookingDetailResponse.builder()
            .bookingReference(source.getBookingId())
            .status(source.getBookingStatus())
            .currency(room.getTotalSettlementRates().getDisplayCurrency())
            .totalAmount(
                new BigDecimal(room.getTotalSettlementRates().getDisplayAmount()).longValue()
            )
            .partnerAmount(
                new BigDecimal(room.getTotalSettlementRates().getPartnerAmount()).longValue()
            )
            .build();

    }
}
