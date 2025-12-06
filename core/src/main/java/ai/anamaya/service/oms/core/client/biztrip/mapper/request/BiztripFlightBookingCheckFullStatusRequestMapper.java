package ai.anamaya.service.oms.core.client.biztrip.mapper.request;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.request.*;
import ai.anamaya.service.oms.core.dto.request.booking.status.FlightBookingStatusCheckRequest;
import java.util.List;

public class BiztripFlightBookingCheckFullStatusRequestMapper {

    public BiztripBookingCheckFullStatusRequest map(
        FlightBookingStatusCheckRequest request
    ) {
        if (request == null || request.getBookingReferenceIds() == null) {
            return new BiztripBookingCheckFullStatusRequest(List.of());
        }

        return new BiztripBookingCheckFullStatusRequest(
            List.copyOf(request.getBookingReferenceIds())
        );
    }
}
