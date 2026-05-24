package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.BookingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundCancelRequest {
    private BookingType type;
    private String partnerBookingId;
    private String bookingId;
    private String remarks;
}
