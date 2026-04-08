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
public class HotelPropertyDetailRequest {

    @NotEmpty(message = "propertyIds cannot be empty")
    private List<String> propertyIds;

}
