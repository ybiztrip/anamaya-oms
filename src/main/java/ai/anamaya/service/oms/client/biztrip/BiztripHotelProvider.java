package ai.anamaya.service.oms.client.biztrip;

import ai.anamaya.service.oms.dto.request.BiztripHotelSearchRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.BiztripHotelResponse;
import ai.anamaya.service.oms.service.HotelProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("biztripHotelProvider")
@RequiredArgsConstructor
public class BiztripHotelProvider implements HotelProvider {

    private final BiztripHotelService biztripHotelService;

    @Override
    public ApiResponse<List<BiztripHotelResponse>> searchHotels(BiztripHotelSearchRequest request) {
        return biztripHotelService.searchHotels(request);
    }
}
