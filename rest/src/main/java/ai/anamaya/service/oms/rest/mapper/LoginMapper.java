package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.LoginRequest;
import ai.anamaya.service.oms.core.dto.response.LoginResponse;
import ai.anamaya.service.oms.rest.dto.request.LoginRequestRest;
import ai.anamaya.service.oms.rest.dto.response.LoginResponseRest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginMapper {

    LoginRequest toCommand(LoginRequestRest dto);

    LoginResponseRest toResponse(LoginResponse result);
}
