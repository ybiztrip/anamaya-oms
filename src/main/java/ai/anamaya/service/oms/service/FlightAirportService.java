package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.FlightAirportResponse;
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
public class FlightAirportService {

    private final WebClient webClient;
    private final BiztripAuthService authService;

    public FlightAirportService(@Qualifier("biztripWebClient") WebClient webClient,
                                BiztripAuthService authService) {
        this.webClient = webClient;
        this.authService = authService;
    }

    public ApiResponse<List<FlightAirportResponse>> getAirports() {
        try {
            String accessToken = authService.getAccessToken();

            Map<String, Object> response = webClient.get()
                .uri("/flight/data/airports")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
    clientResponse -> clientResponse.bodyToMono(String.class)
                    .flatMap(body -> {
                        log.error("Failed to fetch airports: {}", body);
                        return Mono.error(new RuntimeException("Failed to fetch airport data"));
                    })
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(10))
                .block();


            if (response == null || response.get("data") == null) {
                return ApiResponse.error("No airport data found");
            }

            List<FlightAirportResponse> airports = ((List<Map<String, Object>>) response.get("data"))
                    .stream()
                    .map(this::mapToAirportResponse)
                    .toList();

            return ApiResponse.success(airports);

        } catch (WebClientResponseException e) {
            log.error("BizTrip API error: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
            return ApiResponse.error("External API error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching airports", e);
            return ApiResponse.error("Unexpected error: " + e.getMessage());
        }
    }

    private FlightAirportResponse mapToAirportResponse(Map<String, Object> m) {
        return FlightAirportResponse.builder()
                .airportCode((String) m.get("airportCode"))
                .city((String) m.get("city"))
                .countryId((String) m.get("countryId"))
                .countryCode((String) m.get("countryCode"))
                .areaCode((String) m.get("areaCode"))
                .timeZone((String) m.get("timeZone"))
                .internationalAirportName((String) m.get("internationalAirportName"))
                .airportIcaoCode((String) m.get("airportIcaoCode"))
                .localAirportName((String) m.get("localAirportName"))
                .localCityName((String) m.get("localCityName"))
                .countryName((String) m.get("countryName"))
                .build();
    }
}
