package ai.anamaya.service.oms.rest.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BookingRejectRequestRest {
    private List<Long> flightIds;
    private List<Long> hotelIds;
}
