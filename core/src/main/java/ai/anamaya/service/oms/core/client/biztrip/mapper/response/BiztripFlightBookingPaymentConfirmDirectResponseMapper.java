package ai.anamaya.service.oms.core.client.biztrip.mapper.response;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.BiztripBookingPaymentConfirmDirectResponse;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingFlightSubmitResponse;

public class BiztripFlightBookingPaymentConfirmDirectResponseMapper {

    public BookingFlightSubmitResponse map(BiztripBookingPaymentConfirmDirectResponse b) {
        BookingFlightSubmitResponse res = new BookingFlightSubmitResponse();

        res.setBookingSubmissionStatus(b.getPaymentConfirmationStatus());
        res.setBookingId(b.getBookingId());
        return res;
    }

}
