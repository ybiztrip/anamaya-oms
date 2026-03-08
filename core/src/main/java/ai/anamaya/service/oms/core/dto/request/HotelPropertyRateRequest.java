package ai.anamaya.service.oms.core.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelPropertyRateRequest {

    @NotEmpty(message = "propertyIds cannot be empty")
    private List<String> propertyIds;

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

    private String sortBy;

    private Filters filters;

    private PriceRange priceRange;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Filters {

        @Size(min = 5, max = 5)
        private List<@NotNull Boolean> starRating;

        private PriceRange priceRange;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PriceRange {
        private Integer min;
        private Integer max;
    }
}
