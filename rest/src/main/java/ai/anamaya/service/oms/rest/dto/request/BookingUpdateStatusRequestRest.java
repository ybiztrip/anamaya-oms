package ai.anamaya.service.oms.rest.dto.request;

import ai.anamaya.service.oms.core.enums.BookingType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class BookingUpdateStatusRequestRest {
    BookingType type;
    String otaReference;
    String status;
}
