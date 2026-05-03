package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.UpdatePasswordRequest;
import ai.anamaya.service.oms.core.dto.request.UserCreateRequest;
import ai.anamaya.service.oms.core.dto.request.UserGetListRequest;
import ai.anamaya.service.oms.core.dto.request.UserUpdateRequest;
import ai.anamaya.service.oms.core.dto.request.UserRoleItemRequest;
import ai.anamaya.service.oms.core.dto.response.UserResponse;
import ai.anamaya.service.oms.core.dto.response.UserRoleResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.UserService;

import ai.anamaya.service.oms.core.util.SecurityUtil;
import ai.anamaya.service.oms.rest.dto.request.*;
import ai.anamaya.service.oms.rest.dto.response.*;
import ai.anamaya.service.oms.rest.mapper.UserMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserMapper mapper;
    private final JwtUtils jwtUtils;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','COMPANY_ADMIN')")
    @PostMapping
    public ApiResponse<UserResponseRest> create(@Valid @RequestBody UserCreateRequestRest reqRest) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        UserCreateRequest reqCore = mapper.toCore(reqRest);

        UserResponse result = service.create(userCallerContext, reqCore);

        return ApiResponse.success(mapper.toRest(result));
    }

    @PutMapping("/{id}/update-password")
    public ApiResponse<String> updatePassword(
        @PathVariable Long id,
        @Valid @RequestBody UpdatePasswordRequestRest reqRest
    ) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        reqRest.setUserId(id);
        UpdatePasswordRequest reqCore = mapper.toCore(reqRest);

        service.updatePassword(userCallerContext, reqCore);

        return ApiResponse.success("Password updated successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponseRest> update(
        @PathVariable Long id,
        @Valid @RequestBody UserUpdateRequestRest reqRest
    ) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        UserUpdateRequest reqCore = mapper.toCore(reqRest);

        UserResponse result = service.update(userCallerContext, id, reqCore);

        return ApiResponse.success(mapper.toRest(result));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponseRest> getById(@PathVariable Long id) {
        UserResponse result = service.getById(id);

        return ApiResponse.success(mapper.toRest(result));
    }

    @GetMapping
    public ApiResponse<List<UserResponseRest>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort,
        @ModelAttribute UserGetListRequestRest requestRest
    ) {
        Long companyIdFromToken = jwtUtils.getCompanyIdFromToken();
        Long userIdFromToken = jwtUtils.getUserIdFromToken();

        boolean isSuperAdmin = SecurityUtil.hasRole("SUPER_ADMIN");
        boolean isAdmin = SecurityUtil.hasRole("COMPANY_ADMIN");
        if (!isSuperAdmin) {
            requestRest.setCompanyId(companyIdFromToken);
        }

        if (!isAdmin) {
            requestRest.setUserId(userIdFromToken);
        }

        UserGetListRequest request = mapper.toCore(requestRest);
        var pageResult = service.getAll(page, size, sort, request);

        List<UserResponseRest> listRest = pageResult
            .getContent()
            .stream()
            .map(mapper::toRest)
            .toList();

        return ApiResponse.paginatedSuccess(
            listRest,
            pageResult.getTotalElements(),
            pageResult.getTotalPages(),
            pageResult.isLast(),
            pageResult.getSize(),
            pageResult.getNumber()
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success("User deleted successfully");
    }

    @GetMapping("/{id}/roles")
    public ApiResponse<List<UserRoleResponseRest>> getUserRoles(@PathVariable Long id) {
        List<UserRoleResponse> result = service.getUserRoles(id);
        
        List<UserRoleResponseRest> resultRest = result.stream()
                .map(mapper::toRest)
                .toList();

        return ApiResponse.success(resultRest);
    }

    @PreAuthorize("hasAnyRole('COMPANY_ADMIN')")
    @PostMapping("/{id}/roles")
    public ApiResponse<String> updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody List<UserRoleItemRequestRest> requestRests
    ) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        List<UserRoleItemRequest> requests = requestRests.stream()
                .map(mapper::toCore)
                .toList();

        service.updateUserRoles(userCallerContext, id, requests);

        return ApiResponse.success("User roles updated successfully");
    }
}
