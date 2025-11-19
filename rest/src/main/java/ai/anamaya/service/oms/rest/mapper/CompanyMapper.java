package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.CompanyRequest;
import ai.anamaya.service.oms.core.dto.response.CompanyResponse;
import ai.anamaya.service.oms.rest.dto.request.CompanyRequestRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyResponseRest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyRequest toCore(CompanyRequestRest dto);

    CompanyResponseRest toRest(CompanyResponse core);

    List<CompanyResponseRest> toRestList(List<CompanyResponse> list);
}
