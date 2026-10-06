package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.dto.request.TrainAvailabilityRequest;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.TrainAvailabilityResponse;
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
public class BiztripTrainAvailabilityService {

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final JwtUtils jwtUtils;

    public BiztripTrainAvailabilityService(@Qualifier("biztripWebClient") WebClient webClient,
                                           BiztripAuthService authService,
                                           JwtUtils jwtUtils) {
        this.webClient = webClient;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    public ApiResponse<TrainAvailabilityResponse> getAvailability(TrainAvailabilityRequest request) {
        try {
            Long companyId = jwtUtils.getCompanyIdFromToken();
            String accessToken = authService.getAccessToken(companyId);

            Map<String, Object> response = webClient.post()
                    .uri("/train/get-availability")
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("Failed to get train availability: {}", body);
                                        return Mono.error(new RuntimeException("Failed to get train availability"));
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(20))
                    .block();

            if (response == null || response.get("data") == null) {
                return ApiResponse.error("No train availability data found");
            }

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            TrainAvailabilityResponse availabilityResponse = TrainAvailabilityResponse.builder()
                    .schedules((List<Map<String, Object>>) data.get("schedules"))
                    .build();

            return ApiResponse.success(availabilityResponse);

        } catch (WebClientResponseException e) {
            log.error("BizTrip API error: {} - {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            return ApiResponse.error("External API error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching train availability", e);
            return ApiResponse.error("Unexpected error: " + e.getMessage());
        }
    }
}
