package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.UpdateHotelOpenSearchRequest;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.HotelOpenSearchResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.HotelAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hotel/admin/opensearch")
public class HotelAdminController {

    private final HotelAdminService hotelAdminService;
    private final JwtUtils jwtUtils;

    @GetMapping("/{id}")
    public ApiResponse<HotelOpenSearchResponse> getOpenSearch(@PathVariable String id) {
        UserCallerContext callerContext = buildCallerContext();
        HotelOpenSearchResponse response = hotelAdminService.getOpenSearch(callerContext, id);
        return ApiResponse.success(response);
    }

    @PutMapping("/{id}")
    public ApiResponse<HotelOpenSearchResponse> updateOpenSearch(
        @PathVariable String id,
        @Valid @RequestBody UpdateHotelOpenSearchRequest request
    ) {
        UserCallerContext callerContext = buildCallerContext();
        HotelOpenSearchResponse response = hotelAdminService.updateOpenSearch(callerContext, id, request);
        return ApiResponse.success(response);
    }

    private UserCallerContext buildCallerContext() {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        return new UserCallerContext(companyId, userId, userEmail);
    }
}
