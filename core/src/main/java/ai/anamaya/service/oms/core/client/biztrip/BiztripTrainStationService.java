package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.TrainStationResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BiztripTrainStationService {

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final JwtUtils jwtUtils;

    public BiztripTrainStationService(@Qualifier("biztripWebClient") WebClient webClient,
                                      BiztripAuthService authService,
                                      JwtUtils jwtUtils) {
        this.webClient = webClient;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    public ApiResponse<List<TrainStationResponse>> getAllStations() {
        try {
            Long companyId = jwtUtils.getCompanyIdFromToken();
            String accessToken = authService.getAccessToken(companyId);

            Map<String, Object> response = webClient.get()
                    .uri("/train/all-station")
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (response == null || response.get("data") == null) {
                return ApiResponse.error("No station data found");
            }

            List<TrainStationResponse> stations = ((List<Map<String, Object>>) response.get("data"))
                    .stream()
                    .map(this::mapToStationResponse)
                    .toList();

            return ApiResponse.success(stations);

        } catch (WebClientResponseException e) {
            log.error("BizTrip API error: {} - {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            return ApiResponse.error("External API error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching train stations", e);
            return ApiResponse.error("Unexpected error: " + e.getMessage());
        }
    }

    private TrainStationResponse mapToStationResponse(Map<String, Object> m) {
        return TrainStationResponse.builder()
                .stationName((String) m.get("stationName"))
                .stationCode((String) m.get("stationCode"))
                .cityName((String) m.get("cityName"))
                .build();
    }
}
