package ai.anamaya.service.oms.core.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelRateRequest {

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

    @NotEmpty(message = "propertyIds cannot be empty")
    private List<String> propertyIds;
}
