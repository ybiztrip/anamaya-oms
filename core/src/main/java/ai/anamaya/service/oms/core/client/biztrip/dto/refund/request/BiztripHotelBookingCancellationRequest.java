package ai.anamaya.service.oms.core.client.biztrip.dto.refund.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiztripHotelBookingCancellationRequest {
    private String partnerBookingId;
    private String bookingId;
    private String cancellationReason;
}
