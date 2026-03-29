package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.BookingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAttachmentResponse {
    private Long id;
    private Long companyId;
    private Long bookingId;
    private String bookingCode;
    private String file;
    private BookingType type;
}
