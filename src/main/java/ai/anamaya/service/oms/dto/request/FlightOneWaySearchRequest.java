package ai.anamaya.service.oms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightOneWaySearchRequest {

    @NotNull
    private Journey journey;

    @NotNull
    private Passengers passengers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Journey {
        @NotBlank
        private String depAirportOrAreaCode;

        @NotBlank
        private String arrAirportOrAreaCode;

        @NotBlank
        private String depDate;

        @NotBlank
        private String seatClass;

        private String sortBy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Passengers {
        @NotBlank
        private String adult;

        @NotBlank
        private String child;

        @NotBlank
        private String infant;
    }
}
