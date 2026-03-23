package ai.anamaya.service.oms.core.client.biztrip.mapper.request;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.request.*;
import ai.anamaya.service.oms.core.dto.request.booking.payment.FlightBookingPaymentRequest;
import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;

public class BiztripFlightBookingPaymentRequestMapper {

    public BiztripBookingPaymentRequest map(FlightBookingPaymentRequest request) {
        BiztripBookingPaymentRequest dto = new BiztripBookingPaymentRequest();
        dto.setBookingId(request.getBookingId());
        dto.setPaymentMethod(request.getPaymentMethod());

        if(dto.getPaymentMethod() == BookingPaymentMethod.CUST_CREDIT_CARD) {
            BiztripBookingPaymentRequest.CreditCardDetail creditCardDetail =
                new BiztripBookingPaymentRequest.CreditCardDetail();
            creditCardDetail.setCardName(request.getCreditCardDetail().getCardName());
            creditCardDetail.setLastSixDigitNumber(request.getCreditCardDetail().getLastSixDigitNumber());
            dto.setCreditCardDetail(creditCardDetail);
        }

        return dto;
    }

}
