package ai.anamaya.service.oms.core.dto.response.booking.submit;

import lombok.Data;

@Data
public class FareDetail {
    private Price adultFare;
    private Price childFare;
    private Price infantFare;
}
