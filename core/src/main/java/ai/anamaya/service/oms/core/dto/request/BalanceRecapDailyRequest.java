package ai.anamaya.service.oms.core.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BalanceRecapDailyRequest {

    @NotNull
    private LocalDate date;
}