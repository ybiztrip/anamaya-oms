package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.BiztripHotelSearchRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.BiztripHotelResponse;
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

    public ApiResponse<List<BiztripHotelResponse>> searchHotels(String source, BiztripHotelSearchRequest request) {
        return getProvider(source).searchHotels(request);
    }
}
