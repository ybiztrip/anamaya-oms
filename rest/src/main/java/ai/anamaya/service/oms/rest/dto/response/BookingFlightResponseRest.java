package ai.anamaya.service.oms.rest.dto.response;

import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingFlightResponseRest {
    private Long id;
    private Long companyId;
    private Long bookingId;
    private String bookingCode;
    private Short type;
    private String clientSource;
    private String itemId;
    private String origin;
    private String destination;
    private LocalDateTime departureDatetime;
    private LocalDateTime arrivalDatetime;
    private String paymentUrl;
    private BookingFlightStatus status;
    private JsonNode metadata;
    private LocalDateTime createdAt;

    private List<BookingPaxResponseRest> paxs;
}
