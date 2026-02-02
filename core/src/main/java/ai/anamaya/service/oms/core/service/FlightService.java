package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.FlightAddOnsRequest;
import ai.anamaya.service.oms.core.dto.request.FlightOneWaySearchRequest;
import ai.anamaya.service.oms.core.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FlightService {

    private final Map<String, FlightProvider> flightProviders;

    public FlightService(Map<String, FlightProvider> flightProviders) {
        this.flightProviders = flightProviders;
    }

    private FlightProvider getProvider(String source) {
        String key = (source != null ? source.toLowerCase() : "biztrip") + "FlightProvider";
        FlightProvider provider = flightProviders.get(key);

        if (provider == null) {
            log.warn("Provider '{}' not found, fallback to 'biztripFlightProvider'", key);
            provider = flightProviders.get("biztripFlightProvider");
        }

        return provider;
    }

    public ApiResponse<List<FlightAirportResponse>> getAirports(String source) {
        return getProvider(source).getAirports();
    }

    public ApiResponse<List<FlightAirlineResponse>> getAirlines(String source) {
        return getProvider(source).getAirlines();
    }

    public ApiResponse<FlightBookingRuleResponse> getBookingRules(String source, String airline) {
        return getProvider(source).getBookingRules(airline);
    }

    public FlightAddOnsResponse getAddOns(CallerContext callerContext, String source, FlightAddOnsRequest request) {
        return getProvider(source).getAddOns(callerContext, request);
    }


    public ApiResponse<FlightOneWaySearchResponse> searchOneWay(String source, FlightOneWaySearchRequest request) {
        return getProvider(source).searchOneWay(request);
    }
}
