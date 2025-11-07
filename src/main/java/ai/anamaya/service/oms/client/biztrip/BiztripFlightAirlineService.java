package ai.anamaya.service.oms.client.biztrip;

import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.FlightAirlineResponse;
import ai.anamaya.service.oms.security.JwtUtils;
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
public class BiztripFlightAirlineService {

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final JwtUtils jwtUtils;

    public BiztripFlightAirlineService(
            @Qualifier("biztripWebClient") WebClient webClient,
            BiztripAuthService authService,
            JwtUtils jwtUtils
    ) {
        this.webClient = webClient;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    public ApiResponse<List<FlightAirlineResponse>> getAirlines() {
        try {
            Long companyId = jwtUtils.getCompanyIdFromToken();
            String accessToken = authService.getAccessToken(companyId);

            Map<String, Object> response = webClient.get()
                    .uri("/flight/data/airlines")
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("Failed to fetch airlines: {}", body);
                                        return Mono.error(new RuntimeException("Failed to fetch airline data"));
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (response == null || response.get("data") == null) {
                return ApiResponse.error("No airline data found");
            }

            List<FlightAirlineResponse> airlines = ((List<Map<String, Object>>) response.get("data"))
                    .stream()
                    .map(this::mapToAirlineResponse)
                    .toList();

            return ApiResponse.success(airlines);

        } catch (WebClientResponseException e) {
            log.error("BizTrip API error: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
            return ApiResponse.error("External API error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching airlines", e);
            return ApiResponse.error("Unexpected error: " + e.getMessage());
        }
    }

    private FlightAirlineResponse mapToAirlineResponse(Map<String, Object> m) {
        return FlightAirlineResponse.builder()
                .airlineCode((String) m.get("airlineCode"))
                .airlineName((String) m.get("airlineName"))
                .logoUrl((String) m.get("logoUrl"))
                .build();
    }
}
