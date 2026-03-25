package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.dto.request.UpdatePasswordRequest;
import ai.anamaya.service.oms.core.dto.request.UserCreateRequest;
import ai.anamaya.service.oms.core.dto.request.UserGetListRequest;
import ai.anamaya.service.oms.core.dto.request.UserUpdateRequest;
import ai.anamaya.service.oms.core.dto.response.UserResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.UserService;

import ai.anamaya.service.oms.rest.dto.request.*;
import ai.anamaya.service.oms.rest.dto.response.UserResponseRest;
import ai.anamaya.service.oms.rest.dto.response.ApiResponse;
import ai.anamaya.service.oms.rest.mapper.UserMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        UserCreateRequest reqCore = mapper.toCore(reqRest);

        UserResponse result = service.create(reqCore);

        return ApiResponse.success(mapper.toRest(result));
    }

    @PutMapping("/update-password")
    public ApiResponse<String> updatePassword(@Valid @RequestBody UpdatePasswordRequestRest reqRest) {

        UpdatePasswordRequest reqCore = mapper.toCore(reqRest);

        service.updatePassword(reqCore);

        return ApiResponse.success("Password updated successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponseRest> update(
        @PathVariable Long id,
        @Valid @RequestBody UserUpdateRequestRest reqRest
    ) {

        UserUpdateRequest reqCore = mapper.toCore(reqRest);

        UserResponse result = service.update(id, reqCore);

        return ApiResponse.success(mapper.toRest(result));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponseRest> getById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Set<String> roles = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        Long companyIdFromToken = jwtUtils.getCompanyIdFromToken();
        Long userIdFromToken = jwtUtils.getUserIdFromToken();

        if (roles.contains("USER")) {
            requestRest.setUserId(userIdFromToken);
            requestRest.setCompanyId(companyIdFromToken);
        }

        boolean isAdmin = roles.contains("ROLE_COMPANY_ADMIN");
        boolean isApprover = roles.contains("ROLE_APPROVER");
        if (!isAdmin || !isApprover) {
            requestRest.setCompanyId(companyIdFromToken);
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
}
