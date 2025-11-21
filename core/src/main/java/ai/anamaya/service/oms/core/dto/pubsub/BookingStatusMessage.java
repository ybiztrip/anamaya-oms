package ai.anamaya.service.oms.core.dto.pubsub;

import ai.anamaya.service.oms.core.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusMessage {
    private Long bookingId;
    private BookingStatus status;
}
