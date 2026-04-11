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

        BiztripHotelBookingDetailResponse.Room room =
            (source.getRooms() != null && !source.getRooms().isEmpty())
                ? source.getRooms().get(0)
                : null;

        var rates = room != null ? room.getTotalSettlementRates() : null;
        BiztripHotelBookingDetailResponse.CC cc = source.getCcChargeDetail();

        return HotelBookingDetailResponse.builder()
            .bookingReference(source.getBookingId())
            .paymentUrl(cc != null ? cc.getCcPaymentUrl() : null)
            .status(source.getBookingStatus())
            .currency(rates != null ? rates.getDisplayCurrency() : null)
            .totalAmount(rates != null && rates.getDisplayAmount() != null
                ? new BigDecimal(rates.getDisplayAmount()).longValue()
                : 0L)
            .partnerAmount(rates != null && rates.getPartnerAmount() != null
                ? new BigDecimal(rates.getPartnerAmount()).longValue()
                : 0L)
            .build();

    }
}
