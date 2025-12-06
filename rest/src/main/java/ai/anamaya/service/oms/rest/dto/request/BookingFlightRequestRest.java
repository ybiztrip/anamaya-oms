package ai.anamaya.service.oms.rest.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingFlightRequestRest {
    private Long id;
    private boolean deleted;

    private String type;
    private String clientSource;
    private String itemId;
    private String origin;
    private String destination;
    private LocalDateTime departureDatetime;
    private LocalDateTime arrivalDatetime;
    private String status;
}
