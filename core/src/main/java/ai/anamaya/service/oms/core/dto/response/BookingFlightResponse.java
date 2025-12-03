package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFlightResponse {
    private Long id;
    private Long bookingId;
    private Short type;
    private String clientSource;
    private String itemId;
    private String origin;
    private String destination;
    private OffsetDateTime departureDatetime;
    private OffsetDateTime arrivalDatetime;
    private BookingFlightStatus status;
}
