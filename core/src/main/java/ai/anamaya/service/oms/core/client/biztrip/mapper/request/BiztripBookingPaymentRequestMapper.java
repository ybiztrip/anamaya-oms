package ai.anamaya.service.oms.core.client.biztrip.mapper.request;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.request.*;
import ai.anamaya.service.oms.core.dto.request.booking.payment.BookingPaymentRequest;

public class BiztripBookingPaymentRequestMapper {

    public BiztripBookingPaymentRequest map(BookingPaymentRequest request) {
        BiztripBookingPaymentRequest dto = new BiztripBookingPaymentRequest();
        dto.setBookingId(request.getBookingId());
        dto.setPaymentMethod(request.getPaymentMethod());

        return dto;
    }

}
