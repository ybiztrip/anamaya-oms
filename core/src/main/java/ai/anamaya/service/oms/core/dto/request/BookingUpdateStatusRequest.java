package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.BookingType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class BookingUpdateStatusRequest {
    BookingType type;
    String partnerBookingId;
    String status;
}
