package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.biztrip.BiztripHotelOpenSearchService;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.UpdateHotelOpenSearchRequest;
import ai.anamaya.service.oms.core.dto.response.HotelOpenSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotelAdminService {

    private final BiztripHotelOpenSearchService biztripHotelOpenSearchService;

    public HotelOpenSearchResponse getOpenSearch(CallerContext callerContext, String id) {
        return biztripHotelOpenSearchService.getOpenSearch(callerContext, id);
    }

    public HotelOpenSearchResponse updateOpenSearch(CallerContext callerContext, String id, UpdateHotelOpenSearchRequest request) {
        return biztripHotelOpenSearchService.updateOpenSearch(callerContext, id, request);
    }
}
