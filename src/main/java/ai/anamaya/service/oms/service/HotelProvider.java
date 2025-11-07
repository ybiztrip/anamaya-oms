package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.HotelRateRequest;
import ai.anamaya.service.oms.dto.request.HotelSearchRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.HotelRateResponse;
import ai.anamaya.service.oms.dto.response.HotelResponse;

import java.util.List;

public interface HotelProvider {
    ApiResponse<List<HotelResponse>> searchHotels(HotelSearchRequest request);

    ApiResponse<List<HotelRateResponse>> getHotelRates(HotelRateRequest request);
}
