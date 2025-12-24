package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.*;
import ai.anamaya.service.oms.core.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HotelService {

    private final Map<String, HotelProvider> hotelProviders;

    public HotelService(Map<String, HotelProvider> hotelProviders) {
        this.hotelProviders = hotelProviders;
    }

    private HotelProvider getProvider(String source) {
        String key = (source != null ? source.toLowerCase() : "biztrip") + "HotelProvider";
        HotelProvider provider = hotelProviders.get(key);

        if (provider == null) {
            log.warn("Provider '{}' not found, fallback to 'biztripHotelProvider'", key);
            provider = hotelProviders.get("biztripHotelProvider");
        }

        return provider;
    }

    public HotelGeoListResponse getGeoList(CallerContext callerContext, String source, HotelGeoListRequest request) {
        return getProvider(source).getGeoList(callerContext, request);
    }

    public ApiResponse<List<HotelResponse>> searchHotels(String source, HotelSearchRequest request) {
        return getProvider(source).searchHotels(request);
    }

    public HotelDiscoveryResponse discoveryHotels(CallerContext callerContext, String source, HotelDiscoveryRequest request) {
        return getProvider(source).discoveryHotels(callerContext, request);
    }

    public ApiResponse<List<HotelRoomResponse>> getHotelRooms(String source, HotelRoomRequest request) {
        HotelProvider provider = getProvider(source);
        return provider.getHotelRooms(request);
    }

    public ApiResponse<List<HotelRateResponse>> getHotelRates(String source, HotelRateRequest request) {
        return getProvider(source).getHotelRates(request);
    }

    public ApiResponse<HotelRateCheckResponse> checkHotelRate(String source, HotelRateCheckRequest request) {
        HotelProvider provider = getProvider(source);
        return provider.checkHotelRate(request);
    }

}
