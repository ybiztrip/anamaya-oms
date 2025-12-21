package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.dto.request.LoginExternalRequest;
import ai.anamaya.service.oms.core.dto.request.LoginRequest;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.LoginResponse;
import ai.anamaya.service.oms.core.exception.AccessDeniedException;
import ai.anamaya.service.oms.core.service.AuthService;
import ai.anamaya.service.oms.rest.dto.request.LoginExternalRequestRest;
import ai.anamaya.service.oms.rest.dto.request.LoginRequestRest;
import ai.anamaya.service.oms.rest.dto.response.LoginResponseRest;
import ai.anamaya.service.oms.rest.mapper.LoginMapper;
import ai.anamaya.service.oms.rest.properties.ExternalChatEngineProperties;
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
    private final ExternalChatEngineProperties externalChatEngineProperties;

    public AuthController(
        AuthService authService,
        LoginMapper loginMapper,
        ExternalChatEngineProperties externalChatEngineProperties) {
        this.authService = authService;
        this.loginMapper = loginMapper;
        this.externalChatEngineProperties = externalChatEngineProperties;
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

    @PostMapping("/login/external")
    public ResponseEntity<ApiResponse<LoginResponseRest>> loginExternal(
        @Valid @RequestBody LoginExternalRequestRest request
    ) {

        switch (request.getType()) {
            case "ADMIN":
                if(externalChatEngineProperties.getAdminToken().equals(request.getSecret())) {
                    throw new AccessDeniedException("Invalid secret");
                }
                break;
            case "AGENT":
                if (!externalChatEngineProperties.getAgentToken().equals(request.getSecret())) {
                    throw new AccessDeniedException("Invalid secret");
                }
                break;
            default:
                throw new AccessDeniedException("Invalid external type");
        }

        LoginExternalRequest requestCore = loginMapper.toCommand(request);

        LoginResponse resultCore = authService.loginByPhoneNo(requestCore);

        LoginResponseRest responseDto = loginMapper.toResponse(resultCore);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

}
