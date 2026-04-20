package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.CompanyConfigBatchUpdateRequest;
import ai.anamaya.service.oms.core.dto.response.CompanyConfigResponse;
import ai.anamaya.service.oms.rest.dto.request.CompanyConfigBatchUpdateRequestRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyConfigResponseRest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyConfigMapper {

    CompanyConfigBatchUpdateRequest toCore(CompanyConfigBatchUpdateRequestRest dto);

    CompanyConfigResponseRest toRest(CompanyConfigResponse core);

    List<CompanyConfigResponseRest> toRestList(List<CompanyConfigResponse> list);
}
