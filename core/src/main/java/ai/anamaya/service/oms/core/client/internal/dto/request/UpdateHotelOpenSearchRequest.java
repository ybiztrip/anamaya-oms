package ai.anamaya.service.oms.core.client.internal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateHotelOpenSearchRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "star is required")
    private Integer star;

    @NotNull(message = "estimationPrice is required")
    private BigDecimal estimationPrice;

    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "province is required")
    private String province;

    @NotBlank(message = "city is required")
    private String city;

    @NotBlank(message = "countryCode is required")
    private String countryCode;

    @NotBlank(message = "postalCode is required")
    private String postalCode;

    @NotNull(message = "latitude is required")
    private Double latitude;

    @NotNull(message = "longitude is required")
    private Double longitude;

    @NotNull(message = "rank is required")
    private Integer rank;

    @NotBlank(message = "accommodationType is required")
    private String accommodationType;
}
