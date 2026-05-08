package ai.anamaya.service.oms.core.client.biztrip.mapper.response;


import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelBookingCreateResponse;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCreateResponse;

import java.math.BigDecimal;

public class BiztripHotelBookingCreateResponseMapper {

    public HotelBookingCreateResponse map(Boolean success, String errorMessage, BiztripHotelBookingCreateResponse source) {
        if (!Boolean.TRUE.equals(success) || source == null) {
            return HotelBookingCreateResponse.builder()
                .isCancel(true)
                .errorMessage(errorMessage)
                .build();
        }

        Long totalAmount = null;
        String paymentUrl = null;

        if (source.getCcChargeDetail() != null) {
            BiztripHotelBookingCreateResponse.CCChargeDetail cc = source.getCcChargeDetail();

            paymentUrl = cc.getCcPaymentUrl();

            if (cc.getTotalTransactionAmount() != null) {
                totalAmount = new BigDecimal(cc.getTotalTransactionAmount()).longValue();
            }
        }
        else if (source.getTotalChargeableRate() != null) {
            if (source.getTotalChargeableRate().getAmount() != null) {
                totalAmount = new BigDecimal(source.getTotalChargeableRate().getAmount()).longValue();
            }
        }

        return HotelBookingCreateResponse.builder()
            .bookingReference(source.getBookingId())
            .status(source.getBookingStatus())
            .currency(
                source.getTotalChargeableRate() != null
                    ? source.getTotalChargeableRate().getCurrencyCode()
                    : null
            )
            .totalAmount(totalAmount)
            .paymentUrl(paymentUrl)
            .build();
    }
}
