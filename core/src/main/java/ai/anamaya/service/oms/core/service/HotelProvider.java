package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.HotelRateCheckRequest;
import ai.anamaya.service.oms.core.dto.request.HotelRateRequest;
import ai.anamaya.service.oms.core.dto.request.HotelRoomRequest;
import ai.anamaya.service.oms.core.dto.request.HotelSearchRequest;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingCheckRateRequest;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingCreateRequest;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCheckRateResponse;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCreateResponse;

import java.util.List;

public interface HotelProvider {
    ApiResponse<List<HotelResponse>> searchHotels(HotelSearchRequest request);
    ApiResponse<List<HotelRoomResponse>> getHotelRooms(HotelRoomRequest request);
    ApiResponse<List<HotelRateResponse>> getHotelRates(HotelRateRequest request);
    ApiResponse<HotelRateCheckResponse> checkHotelRate(HotelRateCheckRequest request);
    HotelBookingCheckRateResponse checkRate(CallerContext callerContext, HotelBookingCheckRateRequest request);
    HotelBookingCreateResponse create(CallerContext callerContext, HotelBookingCreateRequest request);
}
