package ai.anamaya.service.oms.rest.dto.request;

import ai.anamaya.service.oms.core.enums.DocumentBucketType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DocumentUploadRequestRest {
    @NotNull
    DocumentBucketType type;

    @NotNull
    MultipartFile file;
}
