package ai.anamaya.service.oms.rest.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BookingApproveRequestRest {
    private List<Long> flightIds;
    private List<Long> hotelIds;
}
