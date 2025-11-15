package ai.anamaya.service.oms.dto.response.booking.submit;

import lombok.Data;

@Data
public class FareDetail {
    private Price adultFare;
    private Price childFare;
    private Price infantFare;
}
