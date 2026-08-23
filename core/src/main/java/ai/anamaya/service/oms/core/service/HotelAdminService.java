package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.internal.BiztripHotelOpenSearchService;
import ai.anamaya.service.oms.core.client.biztrip.BiztripHotelPropertyMappingService;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.client.internal.dto.request.UpdateHotelOpenSearchRequest;
import ai.anamaya.service.oms.core.client.internal.dto.response.HotelOpenSearchResponse;
import ai.anamaya.service.oms.core.dto.request.PropertyMappingRequest;
import ai.anamaya.service.oms.core.dto.response.HotelPropertyMappingResponse;
import ai.anamaya.service.oms.core.dto.response.HotelPropertyMappingUpdateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelAdminService {

    private final BiztripHotelOpenSearchService biztripHotelOpenSearchService;
    private final BiztripHotelPropertyMappingService biztripHotelPropertyMappingService;

    public HotelOpenSearchResponse getOpenSearch(CallerContext callerContext, String id) {
        return biztripHotelOpenSearchService.getOpenSearch(callerContext, id);
    }

    public HotelOpenSearchResponse updateOpenSearch(CallerContext callerContext, String id, UpdateHotelOpenSearchRequest request) {
        return biztripHotelOpenSearchService.updateOpenSearch(callerContext, id, request);
    }

    public List<HotelPropertyMappingResponse> getPropertyMapping(CallerContext callerContext, String id) {
        return biztripHotelPropertyMappingService.getPropertyMapping(callerContext, id);
    }

    public HotelPropertyMappingUpdateResponse updatePropertyMapping(CallerContext callerContext, String id, PropertyMappingRequest request) {
        return biztripHotelPropertyMappingService.updatePropertyMapping(callerContext, id, request);
    }
}
