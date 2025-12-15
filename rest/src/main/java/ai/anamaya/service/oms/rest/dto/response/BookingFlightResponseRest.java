package ai.anamaya.service.oms.rest.dto.response;

import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDateTime departureDatetime;
    private LocalDateTime arrivalDatetime;
    private BookingFlightStatus status;
    private List<BookingPaxResponseRest> paxs;
}
