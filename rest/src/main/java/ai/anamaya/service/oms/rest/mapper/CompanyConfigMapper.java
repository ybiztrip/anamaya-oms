package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.CompanyConfigUpdateRequest;
import ai.anamaya.service.oms.core.dto.response.CompanyConfigResponse;
import ai.anamaya.service.oms.rest.dto.request.CompanyConfigUpdateRequestRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyConfigResponseRest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyConfigMapper {

    CompanyConfigUpdateRequest toCore(CompanyConfigUpdateRequestRest dto);

    CompanyConfigResponseRest toRest(CompanyConfigResponse core);

    List<CompanyConfigResponseRest> toRestList(List<CompanyConfigResponse> list);
}
