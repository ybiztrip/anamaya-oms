package ai.anamaya.service.oms.client.biztrip;


import ai.anamaya.service.oms.dto.request.FlightAddOnsRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.FlightAddOnsResponse;
import ai.anamaya.service.oms.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
public class BiztripFlightBookingAddOnsService {

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final JwtUtils jwtUtils;

    public BiztripFlightBookingAddOnsService(
            @Qualifier("biztripWebClient") WebClient webClient,
            BiztripAuthService authService,
            JwtUtils jwtUtils
    ) {
        this.webClient = webClient;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    public ApiResponse<FlightAddOnsResponse> getAddOns(FlightAddOnsRequest request) {
        try {
            Long companyId = jwtUtils.getCompanyIdFromToken();
            String accessToken = authService.getAccessToken(companyId);

            Map<String, Object> response = webClient.post()
                    .uri("/flight/booking/add-ons")
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(15))
                    .block();

            if (response == null || response.get("data") == null) {
                return ApiResponse.error("No add-ons data found");
            }

            FlightAddOnsResponse mapped = mapToResponse((Map<String, Object>) response.get("data"));
            return ApiResponse.success(mapped);

        } catch (WebClientResponseException e) {
            log.error("BizTrip API error: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
            return ApiResponse.error("External API error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching add-ons", e);
            return ApiResponse.error("Unexpected error: " + e.getMessage());
        }
    }

    private FlightAddOnsResponse mapToResponse(Map<String, Object> data) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(data, FlightAddOnsResponse.class);
    }
}
