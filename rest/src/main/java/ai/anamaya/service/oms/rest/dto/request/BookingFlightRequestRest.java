package ai.anamaya.service.oms.rest.dto.request;

import lombok.Data;

@Data
public class BookingFlightRequestRest {
    private Long id;
    private boolean deleted;

    private String type;
    private String clientSource;
    private Long itemId;
    private String origin;
    private String destination;
    private String departureDatetime;
    private String arrivalDatetime;
    private String status;
}
