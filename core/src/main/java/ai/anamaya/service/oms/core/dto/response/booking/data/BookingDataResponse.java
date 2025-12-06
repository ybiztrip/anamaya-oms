package ai.anamaya.service.oms.core.dto.response.booking.data;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookingDataResponse {
    private String bookingReference;
    private String otaReference;
    private String code;
    private String departure;
    private String arrival;
    private String status;
    private BigDecimal adultPrice;
    private BigDecimal childPrice;
    private BigDecimal infantPrice;
    private BigDecimal totalPrice;
}
