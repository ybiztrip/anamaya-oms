package ai.anamaya.service.oms.core.dto.request.booking.status;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightBookingStatusCheckRequest {
    private List<String> bookingReferenceIds;
}
