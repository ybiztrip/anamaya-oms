package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.DocumentBucketType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BookingAttachmentRequest {
    @NotNull
    DocumentBucketType type;

    @NotNull
    MultipartFile file;
}
