package ai.anamaya.service.oms.client.biztrip;

import ai.anamaya.service.oms.dto.request.HotelRateCheckRequest;
import ai.anamaya.service.oms.dto.request.HotelRateRequest;
import ai.anamaya.service.oms.dto.request.HotelSearchRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.HotelRateCheckResponse;
import ai.anamaya.service.oms.dto.response.HotelRateResponse;
import ai.anamaya.service.oms.dto.response.HotelResponse;
import ai.anamaya.service.oms.service.HotelProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("biztripHotelProvider")
@RequiredArgsConstructor
public class BiztripHotelProvider implements HotelProvider {

    private final BiztripHotelService biztripHotelService;
    private final BiztripHotelCheckRateService biztripHotelCheckRateService;
    private final BiztripHotelRateService biztripHotelRateService;

    @Override
    public ApiResponse<List<HotelResponse>> searchHotels(HotelSearchRequest request) {
        return biztripHotelService.searchHotels(request);
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
