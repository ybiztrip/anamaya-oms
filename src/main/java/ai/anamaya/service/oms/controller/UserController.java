package ai.anamaya.service.oms.controller;

import ai.anamaya.service.oms.dto.request.UpdatePasswordRequest;
import ai.anamaya.service.oms.dto.request.UserUpdateRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.request.UserCreateRequest;
import ai.anamaya.service.oms.dto.response.UserResponse;
import ai.anamaya.service.oms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ApiResponse<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return service.create(request);
    }

    @PutMapping("/update-password")
    public ApiResponse<String> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        return service.updatePassword(request);
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return service.update(id, request);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort
    ) {
        return service.getAll(page, size, sort);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        return service.delete(id);
    }

}
