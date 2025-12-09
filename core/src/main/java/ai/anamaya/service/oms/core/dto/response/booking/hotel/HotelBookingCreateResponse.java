package ai.anamaya.service.oms.core.dto.response.booking.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelBookingCreateResponse {

    private String bookingId;
    private String bookingReference;
    private String status;
    private String currency;
    private Long totalAmount;
}
