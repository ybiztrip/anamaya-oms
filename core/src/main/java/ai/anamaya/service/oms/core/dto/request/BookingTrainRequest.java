package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.BookingPaymentMethod;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

// Fields come from one journey + segment of /train/get-availability
@Data
public class BookingTrainRequest {
    @NotBlank
    private String classKey;

    @NotBlank
    private String journeyCode;

    @NotBlank
    private String carrierNumber;

    private String trainName;

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    @NotNull
    private LocalDateTime departureDatetime;

    @NotNull
    private LocalDateTime arrivalDatetime;

    @NotBlank
    private String subClass;

    // availability "Class", e.g. "-"
    private String seatClass;

    @NotNull
    private Integer provider;

    @NotNull
    private BookingPaymentMethod paymentMethod;

    private JsonNode metadata;

    @AssertTrue(message = "departureDatetime must be before arrivalDatetime")
    public boolean isDateValid() {
        return departureDatetime == null || arrivalDatetime == null || departureDatetime.isBefore(arrivalDatetime);
    }
}
