package ai.anamaya.service.oms.core.client.biztrip.mapper.request;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.request.*;
import ai.anamaya.service.oms.core.dto.request.booking.payment.FlightBookingPaymentRequest;

public class BiztripFlightBookingPaymentRequestMapper {

    public BiztripBookingPaymentRequest map(FlightBookingPaymentRequest request) {
        BiztripBookingPaymentRequest dto = new BiztripBookingPaymentRequest();
        dto.setBookingId(request.getBookingId());
        dto.setPaymentMethod(request.getPaymentMethod());

        return dto;
    }

}
