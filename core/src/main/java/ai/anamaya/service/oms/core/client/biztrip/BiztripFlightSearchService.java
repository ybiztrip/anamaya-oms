package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.dto.request.FlightOneWaySearchRequest;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.FlightOneWaySearchResponse;
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

@Slf4j
@Service
public class BiztripFlightSearchService {

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final JwtUtils jwtUtils;

    public BiztripFlightSearchService(
            @Qualifier("biztripWebClient") WebClient webClient,
            BiztripAuthService authService,
            JwtUtils jwtUtils
    ) {
        this.webClient = webClient;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    public ApiResponse<FlightOneWaySearchResponse> searchOneWay(FlightOneWaySearchRequest request) {
        try {
            Long companyId = jwtUtils.getCompanyIdFromToken();
            String accessToken = authService.getAccessToken(companyId);

            Map<String, Object> response = webClient.post()
                    .uri("/flight/search/one-way")
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("Failed to search one-way flights: {}", body);
                                        return Mono.error(new RuntimeException("Failed to search flight"));
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(20))
                    .block();

            if (response == null || response.get("data") == null) {
                return ApiResponse.error("No flight data found");
            }

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            FlightOneWaySearchResponse searchResponse = FlightOneWaySearchResponse.builder()
                    .completed((Boolean) data.get("completed"))
                    .oneWayFlightSearchResults((List<Map<String, Object>>) data.get("oneWayFlightSearchResults"))
                    .build();

            return ApiResponse.success(searchResponse);

        } catch (WebClientResponseException e) {
            log.error("BizTrip API error: {} - {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            return ApiResponse.error("External API error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching flight search", e);
            return ApiResponse.error("Unexpected error: " + e.getMessage());
        }
    }
}
