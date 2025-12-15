package ai.anamaya.service.oms.core.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BookingApproveRequest {
    private List<Long> flightIds;
    private List<Long> hotelIds;
}
