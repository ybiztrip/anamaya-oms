package ai.anamaya.service.oms.core.client.biztrip.mapper.response;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.*;
import ai.anamaya.service.oms.core.dto.response.booking.submit.*;


public class BiztripBookingCheckFullStatusResponseMapper {

    public BookingSubmitResponse map(BiztripCheckFullStatusResponse b) {
        BookingSubmitResponse res = new BookingSubmitResponse();

        if(b.getBookingStatusResult() == null || b.getBookingStatusResult().isEmpty()) {
            return null;
        }

        BiztripCheckFullStatusResult statusResult = b.getBookingStatusResult().get(0);
        res.setBookingSubmissionStatus(statusResult.getStatus());
        res.setBookingId(statusResult.getBookingId());
        return res;
    }

}
