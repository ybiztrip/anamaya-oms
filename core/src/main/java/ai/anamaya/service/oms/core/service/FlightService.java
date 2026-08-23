package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.FlightAddOnsRequest;
import ai.anamaya.service.oms.core.dto.request.FlightOneWaySearchRequest;
import ai.anamaya.service.oms.core.dto.response.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FlightService {

    private static final long AIRPORTS_CACHE_TTL_HOURS = 1;

    private final Map<String, FlightProvider> flightProviders;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public FlightService(Map<String, FlightProvider> flightProviders,
                         RedisTemplate<String, Object> redisTemplate,
                         ObjectMapper objectMapper) {
        this.flightProviders = flightProviders;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
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

    public ApiResponse<List<FlightAirportResponse>> getAirports(String source, String search) {
        List<FlightAirportResponse> airports = getCachedAirports(source);
        if (search == null || search.isBlank()) {
            return ApiResponse.success(airports);
        }
        String q = search.toLowerCase();
        return ApiResponse.success(airports.stream()
                .filter(a -> contains(a.getAirportCode(), q) || contains(a.getCity(), q))
                .toList());
    }

    private List<FlightAirportResponse> getCachedAirports(String source) {
        String key = "oms:flight:airports:" + (source != null ? source.toLowerCase() : "biztrip");
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof String json) {
                return objectMapper.readValue(json, new TypeReference<>() {});
            }
        } catch (Exception e) {
            log.warn("Airport cache read failed for {}, dropping key", source, e);
            redisTemplate.delete(key);
        }
        List<FlightAirportResponse> airports = getProvider(source).getAirports().getData();
        if (airports != null) {
            try {
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(airports),
                        AIRPORTS_CACHE_TTL_HOURS, TimeUnit.HOURS);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                log.warn("Airport cache write failed for {}", source, e);
            }
        }
        return airports != null ? airports : List.of();
    }

    private static boolean contains(String value, String lowerQuery) {
        return value != null && value.toLowerCase().contains(lowerQuery);
    }

    public ApiResponse<List<FlightCityMultipleAirportResponse>> getCityMultipleAirports(String source) {
        return getProvider(source).getCityMultipleAirports();
    }

    public ApiResponse<List<FlightAirportCityResponse>> getAirportsCity(String source, String search) {
        List<FlightAirportCityResponse> result = new java.util.ArrayList<>();

        for (FlightCityMultipleAirportResponse c : getProvider(source).getCityMultipleAirports().getData()) {
            result.add(FlightAirportCityResponse.builder()
                    .airportCode(c.getCityCode())
                    .localAirportName(c.getCity() + " - " + c.getCountry())
                    .localCityName(c.getCity())
                    .countryName(c.getCountry())
                    .build());
        }
        for (FlightAirportResponse a : getCachedAirports(source)) {
            result.add(FlightAirportCityResponse.builder()
                    .airportCode(a.getAirportCode())
                    .localAirportName(a.getInternationalAirportName() + " - (" + a.getCity() + " - " + a.getAirportCode() + ")")
                    .localCityName(a.getCity())
                    .countryName(a.getCountryName())
                    .build());
        }

        if (search == null || search.isBlank()) {
            return ApiResponse.success(result);
        }
        String q = search.toLowerCase();
        return ApiResponse.success(result.stream()
                .filter(r -> contains(r.getAirportCode(), q) || contains(r.getLocalAirportName(), q))
                .toList());
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
