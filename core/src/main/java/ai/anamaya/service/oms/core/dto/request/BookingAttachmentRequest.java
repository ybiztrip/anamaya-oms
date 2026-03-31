package ai.anamaya.service.oms.core.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BookingAttachmentRequest {
    @NotEmpty
    List<String> files;
}
