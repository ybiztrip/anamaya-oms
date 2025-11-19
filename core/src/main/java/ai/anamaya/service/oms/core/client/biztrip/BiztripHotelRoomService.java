package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.dto.request.HotelRoomRequest;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.HotelRoomResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BiztripHotelRoomService {

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final JwtUtils jwtUtils;

    public BiztripHotelRoomService(@Qualifier("biztripWebClient") WebClient webClient,
                                   BiztripAuthService authService,
                                   JwtUtils jwtUtils) {
        this.webClient = webClient;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @SuppressWarnings("unchecked")
    public ApiResponse<List<HotelRoomResponse>> getHotelRooms(HotelRoomRequest request) {
        try {
            Long companyId = jwtUtils.getCompanyIdFromToken();
            String accessToken = authService.getAccessToken(companyId);

            Map<String, Object> response = webClient.post()
                    .uri("/v2/hotel/room")
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("BizTrip Hotel Room API error: {}", body);
                                        return Mono.error(new RuntimeException("Failed to fetch hotel rooms"));
                                    })
                    )
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(15))
                    .block();

            if (response == null || response.get("data") == null) {
                return ApiResponse.error("No hotel room data found");
            }

            List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");
            List<HotelRoomResponse> rooms = dataList.stream()
                    .map(this::mapToHotelRoomResponse)
                    .toList();

            return ApiResponse.success(rooms);

        } catch (WebClientResponseException e) {
            log.error("BizTrip API error: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
            return ApiResponse.error("External API error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching hotel rooms", e);
            return ApiResponse.error("Unexpected error: " + e.getMessage());
        }
    }

    private HotelRoomResponse mapToHotelRoomResponse(Map<String, Object> data) {
        return HotelRoomResponse.builder()
                .roomId((String) data.get("roomId"))
                .propertyId((String) data.get("propertyId"))
                .roomStatus((String) data.get("roomStatus"))
                .roomName((String) data.get("roomName"))
                .roomType((String) data.get("roomType"))
                .bedArrangementData((List<Map<String, Object>>) data.get("bedArrangementData"))
                .imageData((List<Map<String, Object>>) data.get("imageData"))
                .facilityData((List<Map<String, Object>>) data.get("facilityData"))
                .roomView((String) data.get("roomView"))
                .roomWindow((Boolean) data.get("roomWindow"))
                .size((String) data.get("size"))
                .unit((String) data.get("unit"))
                .build();
    }

}
