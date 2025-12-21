package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.*;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.HotelService;
import ai.anamaya.service.oms.rest.dto.request.HotelGeoListRequestRest;
import ai.anamaya.service.oms.rest.dto.response.HotelGeoListResponseRest;
import ai.anamaya.service.oms.rest.mapper.HotelMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hotel")
public class HotelController {

    private final HotelService hotelService;
    private final HotelMapper mapper;
    private final JwtUtils jwtUtils;

    @PostMapping("/geo/list")
    public ApiResponse<HotelGeoListResponseRest> getGeoList(
        @RequestParam(defaultValue = "biztrip") String source,
        @Valid @RequestBody HotelGeoListRequestRest request
    ) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        HotelGeoListRequest reqCore = mapper.toCore(request);
        HotelGeoListResponse response = hotelService.getGeoList(userCallerContext, source, reqCore);

        return ApiResponse.success(mapper.toRest(response));
    }

    @PostMapping("/search")
    public ApiResponse<List<HotelResponse>> searchHotels(
            @RequestParam(defaultValue = "biztrip") String source,
            @Valid @RequestBody HotelSearchRequest request
    ) {
        return hotelService.searchHotels(source, request);
    }

    @PostMapping("/room")
    public ApiResponse<List<HotelRoomResponse>> getHotelRooms(
            @RequestParam(required = false) String source,
            @Valid @RequestBody HotelRoomRequest request
    ) {
        return hotelService.getHotelRooms(source, request);
    }


    @PostMapping("/rate")
    public ApiResponse<List<HotelRateResponse>> getHotelRates(
            @RequestParam(required = false) String source,
            @Valid @RequestBody HotelRateRequest request
    ) {
        return hotelService.getHotelRates(source, request);
    }

    @PostMapping("/rate/check")
    public ApiResponse<HotelRateCheckResponse> checkHotelRate(
            @RequestParam(required = false) String source,
            @Valid @RequestBody HotelRateCheckRequest request
    ) {
        return hotelService.checkHotelRate(source, request);
    }

}
