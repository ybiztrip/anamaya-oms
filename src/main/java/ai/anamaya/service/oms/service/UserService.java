package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.UpdatePasswordRequest;
import ai.anamaya.service.oms.dto.request.UserUpdateRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.request.UserCreateRequest;
import ai.anamaya.service.oms.dto.response.UserResponse;
import ai.anamaya.service.oms.entity.User;
import ai.anamaya.service.oms.exception.NotFoundException;
import ai.anamaya.service.oms.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public ApiResponse<UserResponse> create(UserCreateRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            return ApiResponse.error("Email already exists");
        }

        User user = User.builder()
                .companyId(request.getCompanyId())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .positionId(request.getPositionId())
                .phoneNo(request.getPhoneNo())
                .status(request.getStatus())
                .createdBy(request.getCreatedBy())
                .build();

        repository.save(user);
        return ApiResponse.success(toResponse(user));
    }

    public ApiResponse<UserResponse> update(Long id, UserUpdateRequest request) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        user.setCompanyId(request.getCompanyId());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setGender(request.getGender());
        user.setPositionId(request.getPositionId());
        user.setPhoneNo(request.getPhoneNo());
        user.setStatus(request.getStatus());
        user.setUpdatedBy(request.getCreatedBy());

        repository.save(user);
        return ApiResponse.success(toResponse(user));
    }


    @Transactional
    public ApiResponse<String> updatePassword(UpdatePasswordRequest request) {
        User user = repository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // check if old password matches
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ApiResponse.error("Old password is incorrect");
        }

        // hash the new password and save
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);

        return ApiResponse.success("Password updated successfully");
    }

    public ApiResponse<UserResponse> getById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
        return ApiResponse.success(toResponse(user));
    }

    public ApiResponse<List<UserResponse>> getAll(int page, int size, String sort) {
        Sort sorting = Sort.by("created_at").descending();

        if (sort != null && !sort.isBlank()) {
            String[] sortParams = sort.split(";");
            String sortField = sortParams[0];
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sorting = Sort.by(direction, sortField);
        }

        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<User> users = repository.findAll(pageable);

        List<UserResponse> data = users.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.paginatedSuccess(
                data,
                users.getTotalElements(),
                users.getTotalPages(),
                users.isLast(),
                users.getSize(),
                users.getNumber()
        );
    }

    public ApiResponse<String> delete(Long id) {
        repository.deleteById(id);
        return ApiResponse.success("User deleted successfully");
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .companyId(user.getCompanyId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .positionId(user.getPositionId())
                .phoneNo(user.getPhoneNo())
                .status(user.getStatus())
                .createdBy(user.getCreatedBy())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .updatedBy(user.getUpdatedBy())
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                .build();
    }
}
