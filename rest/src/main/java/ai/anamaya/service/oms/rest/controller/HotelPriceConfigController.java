package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.client.internal.dto.request.HotelPriceConfigSaveRequest;
import ai.anamaya.service.oms.core.client.internal.dto.response.HotelPriceConfigResponse;
import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.HotelAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hotel/admin/price-config")
public class HotelPriceConfigController {

    private final HotelAdminService hotelAdminService;
    private final JwtUtils jwtUtils;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @GetMapping
    public ApiResponse<HotelPriceConfigResponse> getPriceConfig(@RequestParam String accountId) {
        UserCallerContext callerContext = buildCallerContext();
        HotelPriceConfigResponse response = hotelAdminService.getPriceConfig(callerContext, accountId);
        return ApiResponse.success(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @PostMapping
    public ApiResponse<String> savePriceConfig(@Valid @RequestBody HotelPriceConfigSaveRequest request) {
        UserCallerContext callerContext = buildCallerContext();
        String response = hotelAdminService.savePriceConfig(callerContext, request);
        return ApiResponse.success(response);
    }

    private UserCallerContext buildCallerContext() {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        return new UserCallerContext(companyId, userId, userEmail);
    }
}
