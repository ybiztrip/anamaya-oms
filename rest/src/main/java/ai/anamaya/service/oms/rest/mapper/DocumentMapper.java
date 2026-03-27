package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.DocumentUploadRequest;
import ai.anamaya.service.oms.rest.dto.request.DocumentUploadRequestRest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    DocumentUploadRequest toCore(DocumentUploadRequestRest rest);

}
