package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.client.internal.dto.response.AccountResponse;
import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.AccountAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/general/account")
public class AccountAdminController {

    private final AccountAdminService accountAdminService;
    private final JwtUtils jwtUtils;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @GetMapping
    public ApiResponse<List<AccountResponse>> getAccountList() {
        UserCallerContext callerContext = buildCallerContext();
        List<AccountResponse> response = accountAdminService.getAccountList(callerContext);
        return ApiResponse.success(response);
    }

    private UserCallerContext buildCallerContext() {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        return new UserCallerContext(companyId, userId, userEmail);
    }
}
