package ai.anamaya.service.oms.core.client.biztrip.mapper.request;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.request.*;
import ai.anamaya.service.oms.core.dto.request.booking.submit.FlightBookingSearchDataRequest;

import java.util.List;

public class BiztripFlightBookingSearchDataRequestMapper {

    public BiztripBookingSearchDataRequest map(FlightBookingSearchDataRequest request) {

        BiztripBookingSearchDataRequest dto = new BiztripBookingSearchDataRequest();
        dto.setPage(request.getPage());
        dto.setCount(request.getCount());
        dto.setBookingIds(request.getReferenceCodes());
        dto.setClients(List.of());
        dto.setStartDate(request.getStartDate());
        dto.setEndDate(request.getEndDate());

        return dto;
    }
}
