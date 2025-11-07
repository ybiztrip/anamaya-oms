package ai.anamaya.service.oms.client.biztrip;

import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.FlightBookingRuleResponse;
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
import java.util.Map;

@Slf4j
@Service
public class BiztripFlightBookingRuleService {

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final JwtUtils jwtUtils;

    public BiztripFlightBookingRuleService(
            @Qualifier("biztripWebClient") WebClient webClient,
            BiztripAuthService authService,
            JwtUtils jwtUtils
    ) {
        this.webClient = webClient;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    public ApiResponse<FlightBookingRuleResponse> getBookingRules(String airlineCode) {
        try {
            Long companyId = jwtUtils.getCompanyIdFromToken();
            String accessToken = authService.getAccessToken(companyId);

            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/flight/booking/rules")
                            .queryParam("airline", airlineCode)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("Failed to fetch booking rules for airline {}: {}", airlineCode, body);
                                        return Mono.error(new RuntimeException("Failed to fetch booking rules"));
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (response == null || response.get("data") == null) {
                return ApiResponse.error("No booking rule data found");
            }

            Map<String, Object> data = (Map<String, Object>) response.get("data");

            FlightBookingRuleResponse bookingRuleResponse = FlightBookingRuleResponse.builder()
                    .requiresBirthDate((Boolean) data.get("requiresBirthDate"))
                    .requiresDocumentNoForInternational((Boolean) data.get("requiresDocumentNoForInternational"))
                    .requiresNationality((Boolean) data.get("requiresNationality"))
                    .requiresDocumentNoForDomestic((Boolean) data.get("requiresDocumentNoForDomestic"))
                    .requiresId((Boolean) data.get("requiresId"))
                    .build();

            return ApiResponse.success(bookingRuleResponse);

        } catch (WebClientResponseException e) {
            log.error("BizTrip API error: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
            return ApiResponse.error("External API error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching booking rules", e);
            return ApiResponse.error("Unexpected error: " + e.getMessage());
        }
    }
}
