package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request.BiztripHotelGetGeoListRequest;
import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelGetGeoListResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.request.BiztripHotelGeoListRequestMapper;
import ai.anamaya.service.oms.core.client.biztrip.mapper.response.BiztripHotelGeoListResponseMapper;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.HotelGeoListRequest;
import ai.anamaya.service.oms.core.dto.response.HotelGeoListResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class BiztripHotelGeoListService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    private final BiztripHotelGeoListRequestMapper requestMapper =
        new BiztripHotelGeoListRequestMapper();

    private final BiztripHotelGeoListResponseMapper responseMapper =
        new BiztripHotelGeoListResponseMapper();

    public HotelGeoListResponse getGeoList(CallerContext callerContext, HotelGeoListRequest request) {
        try {
            String token = authService.getAccessToken(callerContext.companyId());

            BiztripHotelGetGeoListRequest biztripReq = requestMapper.map(request);

            String rawResponse = biztripWebClient.post()
                .uri("/hotel/geo/list")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(biztripReq)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.debug("Biztrip Hotel raw response: {}", rawResponse);

            JsonNode root = mapper.readTree(rawResponse);

            BiztripHotelGetGeoListResponse biztripResponse =
                mapper.treeToValue(root, BiztripHotelGetGeoListResponse.class);

            return responseMapper.map(biztripResponse);

        } catch (Exception e) {
            log.error("Get geo hotel Biztrip failed", e);
            throw new RuntimeException("Get geo hotel failed: " + e.getMessage());
        }
    }

}
