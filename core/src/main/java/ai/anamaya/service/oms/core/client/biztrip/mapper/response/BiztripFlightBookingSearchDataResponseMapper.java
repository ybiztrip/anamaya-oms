package ai.anamaya.service.oms.core.client.biztrip.mapper.response;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.*;
import ai.anamaya.service.oms.core.dto.response.booking.data.BookingDataResponse;

import java.util.List;

public class BiztripFlightBookingSearchDataResponseMapper {

    public List<BookingDataResponse> map(List<BiztripDataResponse> list) {
        return list.stream()
            .map(b -> {
                BookingDataResponse res = new BookingDataResponse();
                res.setOtaReference(b.getProviderBookingId());
                res.setBookingReference(b.getBookingId());
                res.setStatus(b.getStatus());
                res.setTotalPrice(b.getPrice());

                if (b.getJourney() != null) {
                    String[] journey = b.getJourney().split("-");
                    if (journey.length >= 2) {
                        res.setDeparture(journey[0]);
                        res.setArrival(journey[1]);
                    }
                }

                return res;
            })
            .toList();
    }


}
