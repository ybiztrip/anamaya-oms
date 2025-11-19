package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.dto.request.HotelRateCheckRequest;
import ai.anamaya.service.oms.core.dto.request.HotelRateRequest;
import ai.anamaya.service.oms.core.dto.request.HotelRoomRequest;
import ai.anamaya.service.oms.core.dto.request.HotelSearchRequest;
import ai.anamaya.service.oms.core.dto.response.*;

import java.util.List;

public interface HotelProvider {
    ApiResponse<List<HotelResponse>> searchHotels(HotelSearchRequest request);
    ApiResponse<List<HotelRoomResponse>> getHotelRooms(HotelRoomRequest request);
    ApiResponse<List<HotelRateResponse>> getHotelRates(HotelRateRequest request);
    ApiResponse<HotelRateCheckResponse> checkHotelRate(HotelRateCheckRequest request);
}
