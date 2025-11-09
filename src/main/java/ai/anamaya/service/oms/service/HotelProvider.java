package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.HotelRateCheckRequest;
import ai.anamaya.service.oms.dto.request.HotelRateRequest;
import ai.anamaya.service.oms.dto.request.HotelRoomRequest;
import ai.anamaya.service.oms.dto.request.HotelSearchRequest;
import ai.anamaya.service.oms.dto.response.*;

import java.util.List;

public interface HotelProvider {
    ApiResponse<List<HotelResponse>> searchHotels(HotelSearchRequest request);
    ApiResponse<List<HotelRoomResponse>> getHotelRooms(HotelRoomRequest request);
    ApiResponse<List<HotelRateResponse>> getHotelRates(HotelRateRequest request);
    ApiResponse<HotelRateCheckResponse> checkHotelRate(HotelRateCheckRequest request);
}
