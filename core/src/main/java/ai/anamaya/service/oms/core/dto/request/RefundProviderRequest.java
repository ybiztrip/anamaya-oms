package ai.anamaya.service.oms.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundProviderRequest {
    private String partnerBookingId;
    private String bookingId;
    private String cancellationReason;
}
