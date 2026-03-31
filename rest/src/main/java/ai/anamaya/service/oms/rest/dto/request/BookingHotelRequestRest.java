package ai.anamaya.service.oms.rest.dto.request;

import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingHotelRequestRest {
    private Long id;
    private boolean deleted;

    private String clientSource;
    private String itemId;
    private String roomId;
    private String rateKey;
    private BookingPaymentMethod paymentMethod;
    private String paymentReference1;
    private String paymentReference2;
    private Integer numRoom;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    @AssertTrue(message = "checkInDate must be less than checkOutDate")
    public boolean isDateValid() {
        if (checkInDate == null || checkOutDate == null) {
            return true;
        }
        return checkInDate.isBefore(checkOutDate);
    }

    private String partnerSellAmount;
    private String partnerNettAmount;
    private String currency;
    private String specialRequest;
    private BookingHotelStatus status;
    private JsonNode metadata;
}
