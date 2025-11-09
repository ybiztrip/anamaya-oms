package ai.anamaya.service.oms.client.biztrip;

import ai.anamaya.service.oms.dto.request.HotelRateCheckRequest;
import ai.anamaya.service.oms.dto.request.HotelRateRequest;
import ai.anamaya.service.oms.dto.request.HotelRoomRequest;
import ai.anamaya.service.oms.dto.request.HotelSearchRequest;
import ai.anamaya.service.oms.dto.response.*;
import ai.anamaya.service.oms.service.HotelProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("biztripHotelProvider")
@RequiredArgsConstructor
public class BiztripHotelProvider implements HotelProvider {

    private final BiztripHotelSearchService biztripHotelService;
    private final BiztripHotelRoomService biztripHotelRoomService;
    private final BiztripHotelCheckRateService biztripHotelCheckRateService;
    private final BiztripHotelRateService biztripHotelRateService;

    @Override
    public ApiResponse<List<HotelResponse>> searchHotels(HotelSearchRequest request) {
        return biztripHotelService.searchHotels(request);
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

}
