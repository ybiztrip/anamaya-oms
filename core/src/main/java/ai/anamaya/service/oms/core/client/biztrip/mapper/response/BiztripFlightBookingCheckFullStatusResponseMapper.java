package ai.anamaya.service.oms.core.client.biztrip.mapper.response;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.*;
import ai.anamaya.service.oms.core.dto.response.booking.submit.*;


public class BiztripFlightBookingCheckFullStatusResponseMapper {

    public BookingFlightSubmitResponse map(BiztripCheckFullStatusResponse b) {
        BookingFlightSubmitResponse res = new BookingFlightSubmitResponse();

        if(b.getBookingStatusResult() == null || b.getBookingStatusResult().isEmpty()) {
            return null;
        }

        BiztripCheckFullStatusResult statusResult = b.getBookingStatusResult().get(0);
        res.setBookingSubmissionStatus(statusResult.getStatus());
        res.setBookingId(statusResult.getBookingId());
        return res;
    }

}
