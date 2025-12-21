package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.chatEngine.ChatEngineService;
import ai.anamaya.service.oms.core.dto.request.UpdatePasswordRequest;
import ai.anamaya.service.oms.core.dto.request.UserCreateRequest;
import ai.anamaya.service.oms.core.dto.request.UserUpdateRequest;
import ai.anamaya.service.oms.core.dto.response.UserResponse;
import ai.anamaya.service.oms.core.entity.User;
import ai.anamaya.service.oms.core.exception.NotFoundException;
import ai.anamaya.service.oms.core.repository.UserRepository;
import ai.anamaya.service.oms.core.security.JwtUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ChatEngineService chatEngineClient;

    @Transactional
    public UserResponse create(UserCreateRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        boolean isCompanyAdmin = authorities.stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_COMPANY_ADMIN"));

        if (isCompanyAdmin) {
            Long companyIdFromToken = jwtUtils.getCompanyIdFromToken();
            request.setCompanyId(companyIdFromToken);
        } else if (request.getCompanyId() == null) {
            throw new IllegalArgumentException("companyId is required for SUPER_ADMIN");
        }

        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
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
            .enableChatEngine(request.getEnableChatEngine())
            .build();

        if (Boolean.TRUE.equals(user.getEnableChatEngine())) {
            chatEngineClient.registerUser(user);
        }

        repository.save(user);

        return toResponse(user);
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {

        User user = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));

        user.setCompanyId(request.getCompanyId());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setGender(request.getGender());
        user.setPositionId(request.getPositionId());
        user.setPhoneNo(request.getPhoneNo());
        user.setStatus(request.getStatus());

        repository.save(user);

        return toResponse(user);
    }

    @Transactional
    public void updatePassword(UpdatePasswordRequest request) {

        User user = repository.findById(request.getUserId())
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
    }

    public UserResponse getById(Long id) {
        User user = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));

        return toResponse(user);
    }

    public Page<UserResponse> getAll(int page, int size, String sort) {

        Sort sorting = Sort.by("createdAt").descending(); // fix: createdAt in entity

        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(";");
            String field = parts[0];
            Sort.Direction direction =
                (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            sorting = Sort.by(direction, field);
        }

        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<User> users = repository.findAll(pageable);

        List<UserResponse> mapped =
            users.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PageImpl<>(
            mapped,
            pageable,
            users.getTotalElements()
        );
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("User not found");
        }
        repository.deleteById(id);
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
