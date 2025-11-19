package ai.anamaya.service.oms.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelRateCheckRequest {

    @NotBlank(message = "propertyId is required")
    private String propertyId;

    @NotBlank(message = "roomId is required")
    private String roomId;

    @NotBlank(message = "checkInDate is required")
    private String checkInDate;

    @NotBlank(message = "checkOutDate is required")
    private String checkOutDate;

    @Positive(message = "numRooms must be positive")
    private int numRooms;

    @Positive(message = "numAdults must be positive")
    private int numAdults;

    private Integer numChildren;

    @NotBlank(message = "language is required")
    private String language;

    @NotBlank(message = "displayCurrency is required")
    private String displayCurrency;

    @NotBlank(message = "userNationality is required")
    private String userNationality;

    @NotBlank(message = "rateKey is required")
    private String rateKey;
}
