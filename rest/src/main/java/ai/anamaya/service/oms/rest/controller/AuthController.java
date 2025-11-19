package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.dto.request.LoginRequest;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.LoginResponse;
import ai.anamaya.service.oms.core.service.AuthService;
import ai.anamaya.service.oms.rest.dto.request.LoginRequestRest;
import ai.anamaya.service.oms.rest.dto.response.LoginResponseRest;
import ai.anamaya.service.oms.rest.mapper.LoginMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final LoginMapper loginMapper;

    public AuthController(AuthService authService, LoginMapper loginMapper) {
        this.authService = authService;
        this.loginMapper = loginMapper;
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseRest>> login(
        @Valid @RequestBody LoginRequestRest request
    ) {

        LoginRequest requestCore = loginMapper.toCommand(request);

        LoginResponse resultCore = authService.login(requestCore);

        LoginResponseRest responseDto = loginMapper.toResponse(resultCore);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

}
