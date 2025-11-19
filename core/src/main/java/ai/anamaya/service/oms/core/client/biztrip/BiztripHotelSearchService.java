package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.dto.request.HotelSearchRequest;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.HotelResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
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
public class BiztripHotelSearchService {

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final JwtUtils jwtUtils;

    public BiztripHotelSearchService(@Qualifier("biztripWebClient") WebClient webClient,
                                     BiztripAuthService authService,
                                     JwtUtils jwtUtils) {
        this.webClient = webClient;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    public ApiResponse<List<HotelResponse>> searchHotels(HotelSearchRequest request) {
        try {
            Long companyId = jwtUtils.getCompanyIdFromToken();
            String accessToken = authService.getAccessToken(companyId);

            Map<String, Object> response = webClient.post()
                    .uri("/v2/hotel")
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("Failed to fetch hotels: {}", body);
                                        return Mono.error(new RuntimeException("Failed to fetch hotel data"));
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(15))
                    .block();

            if (response == null || response.get("data") == null) {
                return ApiResponse.error("No hotel data found");
            }

            List<Map<String, Object>> hotelData = (List<Map<String, Object>>) response.get("data");

            List<HotelResponse> hotels = hotelData.stream()
                    .map(this::mapToHotelResponse)
                    .collect(Collectors.toList());

            return ApiResponse.success(hotels);

        } catch (WebClientResponseException e) {
            log.error("BizTrip API error: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
            return ApiResponse.error("External API error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching hotels", e);
            return ApiResponse.error("Unexpected error: " + e.getMessage());
        }
    }

    private HotelResponse mapToHotelResponse(Map<String, Object> m) {
        return HotelResponse.builder()
                .id((String) m.get("id"))
                .status((String) m.get("status"))
                .name((String) m.get("name"))
                .latitude(m.get("latitude") != null ? ((Number) m.get("latitude")).doubleValue() : null)
                .longitude(m.get("longitude") != null ? ((Number) m.get("longitude")).doubleValue() : null)
                .lineData((List<String>) m.get("lineData"))
                .city((String) m.get("city"))
                .province((String) m.get("province"))
                .postalCode((String) m.get("postalCode"))
                .country((String) m.get("country"))
                .star(m.get("star") != null ? ((Number) m.get("star")).intValue() : null)
                .accommodationType((String) m.get("accommodationType"))
                .propertyImageData((List<Map<String, Object>>) m.get("propertyImageData"))
                .facilityData((List<Map<String, Object>>) m.get("facilityData"))
                .build();
    }
}
