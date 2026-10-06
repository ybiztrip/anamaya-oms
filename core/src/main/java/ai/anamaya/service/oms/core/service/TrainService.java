package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.client.biztrip.BiztripTrainAvailabilityService;
import ai.anamaya.service.oms.core.client.biztrip.BiztripTrainStationService;
import ai.anamaya.service.oms.core.dto.request.TrainAvailabilityRequest;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.TrainAvailabilityResponse;
import ai.anamaya.service.oms.core.dto.response.TrainStationResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainService {

    private static final long STATIONS_CACHE_TTL_HOURS = 1;
    private static final String CACHE_KEY = "oms:train:stations:biztrip";

    private final BiztripTrainStationService trainStationService;
    private final BiztripTrainAvailabilityService trainAvailabilityService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public ApiResponse<TrainAvailabilityResponse> getAvailability(TrainAvailabilityRequest request) {
        return trainAvailabilityService.getAvailability(request);
    }

    public ApiResponse<List<TrainStationResponse>> getAllStations() {
        try {
            Object cached = redisTemplate.opsForValue().get(CACHE_KEY);
            if (cached instanceof String json) {
                return ApiResponse.success(objectMapper.readValue(json, new TypeReference<>() {}));
            }
        } catch (Exception e) {
            log.warn("Train station cache read failed, dropping key", e);
            redisTemplate.delete(CACHE_KEY);
        }

        ApiResponse<List<TrainStationResponse>> response = trainStationService.getAllStations();
        if (response.isSuccess() && response.getData() != null) {
            try {
                redisTemplate.opsForValue().set(CACHE_KEY, objectMapper.writeValueAsString(response.getData()),
                        STATIONS_CACHE_TTL_HOURS, TimeUnit.HOURS);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                log.warn("Train station cache write failed", e);
            }
        }
        return response;
    }
}
