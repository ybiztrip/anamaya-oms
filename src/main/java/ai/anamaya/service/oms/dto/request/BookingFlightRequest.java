package ai.anamaya.service.oms.dto.request;

import lombok.*;
import java.time.LocalDateTime;

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
    private LocalDateTime departureDatetime;
    private LocalDateTime arrivalDatetime;
    private Short status;
}
