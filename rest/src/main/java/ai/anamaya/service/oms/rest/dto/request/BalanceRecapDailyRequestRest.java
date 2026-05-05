package ai.anamaya.service.oms.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BalanceRecapDailyRequestRest {

    @NotBlank
    private String date;
}