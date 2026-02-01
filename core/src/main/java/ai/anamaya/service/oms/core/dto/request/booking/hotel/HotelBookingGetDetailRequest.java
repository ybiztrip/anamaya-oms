package ai.anamaya.service.oms.core.dto.request.booking.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelBookingGetDetailRequest {

    private String bookingId;

    private String partnerBookingId;

}
