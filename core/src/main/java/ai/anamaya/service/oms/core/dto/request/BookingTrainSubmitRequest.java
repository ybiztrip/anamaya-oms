package ai.anamaya.service.oms.core.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BookingTrainSubmitRequest {
    private BookingTrainRequest train;
    private List<BookingPaxRequest> paxs;
}
