package ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiztripHotelDiscoveryRequest {

    private String geoId;
    private String checkInDate;
    private String checkOutDate;
    private Integer numRooms;
    private String displayCurrency;
    private String sortBy;
    private Integer page;
    private Integer limit;
    private Filters filters;

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
