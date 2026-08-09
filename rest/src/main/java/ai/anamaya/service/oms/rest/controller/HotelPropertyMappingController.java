package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.PropertyMappingRequest;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.HotelPropertyMappingResponse;
import ai.anamaya.service.oms.core.dto.response.HotelPropertyMappingUpdateResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.HotelAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hotel/admin/property-mapping")
public class HotelPropertyMappingController {

    private final HotelAdminService hotelAdminService;
    private final JwtUtils jwtUtils;

    @GetMapping("/{id}")
    public ApiResponse<List<HotelPropertyMappingResponse>> getPropertyMapping(@PathVariable String id) {
        UserCallerContext callerContext = buildCallerContext();
        List<HotelPropertyMappingResponse> response = hotelAdminService.getPropertyMapping(callerContext, id);
        return ApiResponse.success(response);
    }

    @PostMapping("/{id}")
    public ApiResponse<HotelPropertyMappingUpdateResponse> updatePropertyMapping(
        @PathVariable String id,
        @Valid @RequestBody PropertyMappingRequest request
    ) {
        UserCallerContext callerContext = buildCallerContext();
        HotelPropertyMappingUpdateResponse response = hotelAdminService.updatePropertyMapping(callerContext, id, request);
        return ApiResponse.success(response);
    }

    private UserCallerContext buildCallerContext() {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        return new UserCallerContext(companyId, userId, userEmail);
    }
}
