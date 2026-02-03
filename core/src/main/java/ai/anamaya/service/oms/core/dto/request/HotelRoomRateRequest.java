package ai.anamaya.service.oms.core.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HotelRoomRateRequest {

    @NotBlank(message = "checkInDate is required")
    private String checkInDate;

    @NotBlank(message = "checkOutDate is required")
    private String checkOutDate;

    @NotBlank(message = "language is required")
    private String language;

    @NotBlank(message = "userNationality is required")
    private String userNationality;

    @Positive(message = "numRooms must be positive")
    private int numRooms;

    @Positive(message = "numAdults must be positive")
    private int numAdults;

    private int numChildren = 0;

    @NotBlank(message = "displayCurrency is required")
    private String displayCurrency;

    private boolean isExtended;

    @NotEmpty(message = "propertyId cannot be empty")
    private String propertyId;
}
