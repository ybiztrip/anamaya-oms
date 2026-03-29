package ai.anamaya.service.oms.rest.dto.response;

import ai.anamaya.service.oms.core.enums.BookingType;
import lombok.Data;

@Data
public class BookingAttachmentResponseRest {
    private Long id;
    private Long companyId;
    private Long bookingId;
    private String bookingCode;
    private String file;
    private BookingType type;
}
