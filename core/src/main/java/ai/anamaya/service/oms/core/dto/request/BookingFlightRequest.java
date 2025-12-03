package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import lombok.*;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFlightRequest {
    private Long id;
    private boolean deleted;

    private Short type;
    private String clientSource;
    private String itemId;
    private String origin;
    private String destination;
    private OffsetDateTime departureDatetime;
    private OffsetDateTime arrivalDatetime;
    private BookingFlightStatus status;
}
