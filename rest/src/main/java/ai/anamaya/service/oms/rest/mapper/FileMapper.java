package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.FileFetchRequest;
import ai.anamaya.service.oms.rest.dto.request.FileFetchRequestRest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {
    FileFetchRequest toCore(FileFetchRequestRest rest);
}
