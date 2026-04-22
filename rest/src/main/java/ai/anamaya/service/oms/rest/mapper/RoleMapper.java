package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.response.RoleResponse;
import ai.anamaya.service.oms.rest.dto.response.RoleResponseRest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponseRest toRest(RoleResponse core);
}
