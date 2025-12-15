package ai.anamaya.service.oms.core.dto.pubsub;

import ai.anamaya.service.oms.core.enums.BookingStatus;
import ai.anamaya.service.oms.core.enums.BookingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusMessage {
    private Long companyId;
    private BookingType bookingType;
    private Long bookingId;
    private String bookingCode;
    private BookingStatus status;
}
