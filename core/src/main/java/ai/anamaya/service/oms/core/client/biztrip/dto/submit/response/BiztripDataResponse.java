package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BiztripDataResponse {
    private String bookingId;
    private String partnerBookingId;
    private String providerBookingId;
    private String journey;
    private BigDecimal price;
    private String status;
}
