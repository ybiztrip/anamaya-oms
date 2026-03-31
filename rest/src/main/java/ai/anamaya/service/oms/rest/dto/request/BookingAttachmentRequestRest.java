package ai.anamaya.service.oms.rest.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BookingAttachmentRequestRest {
    @NotEmpty
    List<String> files;
}
