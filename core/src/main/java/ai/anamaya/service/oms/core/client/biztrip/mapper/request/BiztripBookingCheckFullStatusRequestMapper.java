package ai.anamaya.service.oms.core.client.biztrip.mapper.request;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.request.*;
import ai.anamaya.service.oms.core.dto.request.booking.status.BookingStatusCheckRequest;
import java.util.List;

public class BiztripBookingCheckFullStatusRequestMapper {

    public BiztripBookingCheckFullStatusRequest map(
        BookingStatusCheckRequest request
    ) {
        if (request == null || request.getBookingReferenceIds() == null) {
            return new BiztripBookingCheckFullStatusRequest(List.of());
        }

        return new BiztripBookingCheckFullStatusRequest(
            List.copyOf(request.getBookingReferenceIds())
        );
    }
}
