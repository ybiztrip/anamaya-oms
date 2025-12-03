package ai.anamaya.service.oms.rest.dto.response;

import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFlightResponseRest {
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
