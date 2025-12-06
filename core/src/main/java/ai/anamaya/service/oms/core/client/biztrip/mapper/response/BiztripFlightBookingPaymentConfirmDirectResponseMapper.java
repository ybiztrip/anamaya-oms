package ai.anamaya.service.oms.core.client.biztrip.mapper.response;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.BiztripBookingPaymentConfirmDirectResponse;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingSubmitResponse;

public class BiztripFlightBookingPaymentConfirmDirectResponseMapper {

    public BookingSubmitResponse map(BiztripBookingPaymentConfirmDirectResponse b) {
        BookingSubmitResponse res = new BookingSubmitResponse();

        res.setBookingSubmissionStatus(b.getPaymentConfirmationStatus());
        res.setBookingId(b.getBookingId());
        return res;
    }

}
