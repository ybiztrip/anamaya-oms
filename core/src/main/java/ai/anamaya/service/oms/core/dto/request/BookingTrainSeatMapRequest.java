package ai.anamaya.service.oms.core.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingTrainSeatMapRequest {
    @NotNull
    private Long trainId;
}
