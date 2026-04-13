package ai.anamaya.service.oms.rest.dto.request;

import ai.anamaya.service.oms.core.enums.BookingType;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FileFetchRequestRest {
    BookingType type;

    @NotEmpty
    String partnerBookingId;
}
