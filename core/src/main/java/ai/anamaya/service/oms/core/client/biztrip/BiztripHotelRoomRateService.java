package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelRoomRateResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.response.BiztripHotelRoomRateResponseMapper;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.HotelRoomRateRequest;
import ai.anamaya.service.oms.core.dto.response.HotelRoomRateResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BiztripHotelRoomRateService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;
    private final BiztripHotelRoomRateResponseMapper responseMapper = new BiztripHotelRoomRateResponseMapper();

    public List<HotelRoomRateResponse> getHotelRoomRate(CallerContext callerContext, HotelRoomRateRequest request) {
        try {
            String token = authService.getAccessToken(callerContext.companyId());

            String rawResponse  = biztripWebClient.post()
                .uri("/hotel/room-rate")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            JsonNode root = mapper.readTree(rawResponse);
            boolean success = root.path("success").asBoolean(false);
            JsonNode dataNode = root.get("data");
            List<BiztripHotelRoomRateResponse> biztripResponse =
                mapper.readValue(
                    dataNode.traverse(),
                    new com.fasterxml.jackson.core.type.TypeReference<List<BiztripHotelRoomRateResponse>>() {}
                );

            return responseMapper.map(success, biztripResponse);
        } catch (Exception e) {
            log.error("Search hotel room rate to Biztrip failed", e);
            throw new RuntimeException("Search hotel room rate failed: " + e.getMessage());
        }
    }

}
