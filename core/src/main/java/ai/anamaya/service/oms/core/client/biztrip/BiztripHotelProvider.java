package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.*;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingCheckRateRequest;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingCreateRequest;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingGetDetailRequest;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCheckRateResponse;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCreateResponse;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingDetailResponse;
import ai.anamaya.service.oms.core.service.HotelProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("biztripHotelProvider")
@RequiredArgsConstructor
public class BiztripHotelProvider implements HotelProvider {

    private final BiztripHotelGeoListService biztripHotelGeoListService;
    private final BiztripHotelSearchService biztripHotelService;
    private final BiztripHotelDiscoveryService biztripHotelDiscoveryService;
    private final BiztripHotelRoomService biztripHotelRoomService;
    private final BiztripHotelCheckRateService biztripHotelCheckRateService;
    private final BiztripHotelRateService biztripHotelRateService;
    private final BiztripHotelBookingCheckRateService biztripHotelBookingCheckRateService;
    private final BiztripHotelBookingCreateService biztripHotelBookingCreateService;
    private final BiztripHotelGetDetailService biztripHotelGetDetailService;

    @Override
    public HotelGeoListResponse getGeoList(CallerContext callerContext, HotelGeoListRequest request) {
        return biztripHotelGeoListService.getGeoList(callerContext, request);
    }

    @Override
    public ApiResponse<List<HotelResponse>> searchHotels(HotelSearchRequest request) {
        return biztripHotelService.searchHotels(request);
    }

    @Override
    public HotelDiscoveryResponse discoveryHotels(CallerContext callerContext, HotelDiscoveryRequest request) {
        return biztripHotelDiscoveryService.discovery(callerContext, request);
    }

    @Override
    public ApiResponse<List<HotelRoomResponse>> getHotelRooms(HotelRoomRequest request) {
        return biztripHotelRoomService.getHotelRooms(request);
    }

    @Override
    public ApiResponse<List<HotelRateResponse>> getHotelRates(HotelRateRequest request) {
        return biztripHotelRateService.getHotelRates(request);
    }

    @Override
    public ApiResponse<HotelRateCheckResponse> checkHotelRate(HotelRateCheckRequest request) {
        return biztripHotelCheckRateService.checkHotelRate(request);
    }

    @Override
    public HotelBookingCheckRateResponse checkRate(CallerContext callerContext, HotelBookingCheckRateRequest request) {
        return biztripHotelBookingCheckRateService.checkRate(callerContext, request);
    }

    @Override
    public HotelBookingCreateResponse create(CallerContext callerContext, HotelBookingCreateRequest request) {
        return biztripHotelBookingCreateService.create(callerContext, request);
    }

    @Override
    public HotelBookingDetailResponse getBookingDetail(CallerContext callerContext, HotelBookingGetDetailRequest request) {
        return biztripHotelGetDetailService.getDetail(callerContext, request);
    }

}
