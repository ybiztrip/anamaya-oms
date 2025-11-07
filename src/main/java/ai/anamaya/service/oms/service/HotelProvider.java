package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.BiztripHotelSearchRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.BiztripHotelResponse;

import java.util.List;

public interface HotelProvider {
    ApiResponse<List<BiztripHotelResponse>> searchHotels(BiztripHotelSearchRequest request);
}
