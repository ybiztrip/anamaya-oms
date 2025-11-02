package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.LoginRequest;
import ai.anamaya.service.oms.dto.response.LoginResponse;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.entity.User;
import ai.anamaya.service.oms.repository.UserRepository;
import ai.anamaya.service.oms.exception.NotFoundException;
import ai.anamaya.service.oms.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public ApiResponse<LoginResponse> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new NotFoundException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getCompanyId(), user.getEmail());

        LoginResponse loginResponse = LoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .token(token)
                .build();

        return ApiResponse.success(loginResponse);
    }
}
