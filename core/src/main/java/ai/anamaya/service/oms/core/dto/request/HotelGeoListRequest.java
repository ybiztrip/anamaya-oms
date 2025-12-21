package ai.anamaya.service.oms.core.dto.request;

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
public class HotelGeoListRequest {

    @NotBlank(message = "Country code is required")
    private String countryCode;

    @NotBlank(message = "Key is required")
    private String key;

    @NotNull(message = "Offset is required")
    private Integer offset;

    @NotNull(message = "Limit is required")
    private Integer limit;

}
