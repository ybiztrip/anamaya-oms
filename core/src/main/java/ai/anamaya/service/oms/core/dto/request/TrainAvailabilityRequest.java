package ai.anamaya.service.oms.core.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
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
public class TrainAvailabilityRequest {

    @NotNull
    @Valid
    private Route route;

    @NotNull
    private Integer adult;

    private Integer infant;

    // 0 All Class, 1 Executive, 2 Business, 3 Economy
    @NotNull
    @JsonProperty("class")
    private Integer seatClass;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Route {
        @NotBlank
        private String origin;

        @NotBlank
        private String destination;

        @NotBlank
        private String departDate;
    }
}
