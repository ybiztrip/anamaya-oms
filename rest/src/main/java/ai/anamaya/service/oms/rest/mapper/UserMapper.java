package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.UserCreateRequest;
import ai.anamaya.service.oms.core.dto.request.UserUpdateRequest;
import ai.anamaya.service.oms.core.dto.request.UpdatePasswordRequest;
import ai.anamaya.service.oms.core.dto.response.UserResponse;

import ai.anamaya.service.oms.rest.dto.request.UserCreateRequestRest;
import ai.anamaya.service.oms.rest.dto.request.UserUpdateRequestRest;
import ai.anamaya.service.oms.rest.dto.request.UpdatePasswordRequestRest;
import ai.anamaya.service.oms.rest.dto.response.UserResponseRest;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserCreateRequest toCore(UserCreateRequestRest dto);

    UserUpdateRequest toCore(UserUpdateRequestRest dto);

    UpdatePasswordRequest toCore(UpdatePasswordRequestRest dto);

    UserResponseRest toRest(UserResponse core);
}
