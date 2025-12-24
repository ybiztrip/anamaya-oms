package ai.anamaya.service.oms.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDiscoveryRequestRest {

    @NotBlank(message = "Geo id is required")
    private String geoId;

    @NotNull(message = "Check in date is required")
    private LocalDate checkInDate;

    @NotNull(message = "Check out date is required")
    private LocalDate checkOutDate;

    @NotNull(message = "Num rooms is required")
    private Integer numRooms;

    private String displayCurrency;

    private String sortBy;

    private String cursor;

    @NotNull(message = "Page is required")
    private Integer page;

    @NotNull(message = "Limit is required")
    private Integer limit;

    private filters filters;

    @Data
    public static class filters {

        @Size(min = 5, max = 5, message = "Stars must have exactly 5 elements (true/false for 1–5 stars)")
        @NotNull(message = "Stars cannot be null")
        private List<@NotNull Boolean> starRating;

        private priceRange priceRange;

    }

    @Data
    public static class priceRange {
        private Integer min;
        private Integer max;
    }
}
