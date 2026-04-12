package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFlightResponse {
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
    private BookingFlightStatus status;
    private JsonNode metadata;
    private String paymentUrl;
    private LocalDateTime createdAt;

    private List<BookingPaxResponse> paxs;
}
