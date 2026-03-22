package ai.anamaya.service.oms.rest.dto.request;

import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
import jakarta.validation.constraints.AssertTrue;
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
    private BookingPaymentMethod paymentMethod;
    private String paymentReference1;
    private String paymentReference2;

    @AssertTrue(message = "departureDatetime must be less than arrivalDatetime")
    public boolean isDateValid() {
        if (departureDatetime == null || arrivalDatetime == null) {
            return true;
        }
        return departureDatetime.isBefore(arrivalDatetime);
    }

}
