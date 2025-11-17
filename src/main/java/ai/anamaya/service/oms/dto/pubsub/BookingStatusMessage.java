package ai.anamaya.service.oms.dto.pubsub;

import ai.anamaya.service.oms.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingStatusMessage {
    private Long bookingId;
    private BookingStatus status;
}
