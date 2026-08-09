package ai.anamaya.service.oms.core.dto.request;

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

    @NotNull(message = "star is required")
    private Integer star;

    @NotNull(message = "estimationPrice is required")
    private BigDecimal estimationPrice;
}
