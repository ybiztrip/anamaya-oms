package ai.anamaya.service.oms.core.dto.request;

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
public class HotelSearchRequest {

    @NotBlank(message = "Area is required")
    private String area;

    @NotBlank(message = "Count is required")
    private String count;

    private String key;

    @NotNull(message = "Page is required")
    private Integer page;

    @Size(min = 5, max = 5, message = "Stars must have exactly 5 elements (true/false for 1–5 stars)")
    @NotNull(message = "Stars cannot be null")
    private List<@NotNull Boolean> stars;
}
