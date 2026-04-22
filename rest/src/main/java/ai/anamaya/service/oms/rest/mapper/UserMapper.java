package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.UserCreateRequest;
import ai.anamaya.service.oms.core.dto.request.UserGetListRequest;
import ai.anamaya.service.oms.core.dto.request.UserUpdateRequest;
import ai.anamaya.service.oms.core.dto.request.UpdatePasswordRequest;
import ai.anamaya.service.oms.core.dto.response.UserResponse;

import ai.anamaya.service.oms.rest.dto.request.UserCreateRequestRest;
import ai.anamaya.service.oms.rest.dto.request.UserGetListRequestRest;
import ai.anamaya.service.oms.rest.dto.request.UserUpdateRequestRest;
import ai.anamaya.service.oms.rest.dto.request.UpdatePasswordRequestRest;
import ai.anamaya.service.oms.rest.dto.request.UserRoleItemRequestRest;
import ai.anamaya.service.oms.rest.dto.response.UserRoleResponseRest;
import ai.anamaya.service.oms.rest.dto.response.UserResponseRest;

import ai.anamaya.service.oms.core.dto.request.UserRoleItemRequest;
import ai.anamaya.service.oms.core.dto.response.UserRoleResponse;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserCreateRequest toCore(UserCreateRequestRest dto);

    UserUpdateRequest toCore(UserUpdateRequestRest dto);

    UpdatePasswordRequest toCore(UpdatePasswordRequestRest dto);

    UserGetListRequest toCore(UserGetListRequestRest dto);
    UserResponseRest toRest(UserResponse core);

    UserRoleItemRequest toCore(UserRoleItemRequestRest dto);
    UserRoleResponseRest toRest(UserRoleResponse core);
}
