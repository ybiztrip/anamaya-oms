package ai.anamaya.service.oms.client.biztrip;

import ai.anamaya.service.oms.dto.request.HotelRateRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.HotelRateResponse;
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
public class BiztripHotelRateService {

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final JwtUtils jwtUtils;

    public BiztripHotelRateService(@Qualifier("biztripWebClient") WebClient webClient,
                               BiztripAuthService authService,
                               JwtUtils jwtUtils) {
        this.webClient = webClient;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    public ApiResponse<List<HotelRateResponse>> getHotelRates(HotelRateRequest request) {
        try {
            Long companyId = jwtUtils.getCompanyIdFromToken();
            String accessToken = authService.getAccessToken(companyId);

            Map<String, Object> response = webClient.post()
                    .uri("/v2/hotel/rate")
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("Failed to fetch hotel rates: {}", body);
                                        return Mono.error(new RuntimeException("Failed to fetch hotel rate data"));
                                    })
                    )
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(15))
                    .block();

            if (response == null || response.get("data") == null) {
                return ApiResponse.error("No hotel rate data found");
            }

            List<Map<String, Object>> rateData = (List<Map<String, Object>>) response.get("data");

            List<HotelRateResponse> rates = rateData.stream()
                    .map(this::mapToHotelRateResponse)
                    .toList();

            return ApiResponse.success(rates);

        } catch (WebClientResponseException e) {
            log.error("BizTrip Hotel Rate API error: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
            return ApiResponse.error("External API error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching hotel rates", e);
            return ApiResponse.error("Unexpected error: " + e.getMessage());
        }
    }

    private HotelRateResponse mapToHotelRateResponse(Map<String, Object> m) {
        return HotelRateResponse.builder()
                .propertyId((String) m.get("propertyId"))
                .rateStatus((String) m.get("rateStatus"))
                .roomId((String) m.get("roomId"))
                .roomName((String) m.get("roomName"))
                .roomType((String) m.get("roomType"))
                .numRooms(((Number) m.getOrDefault("numRooms", 0)).intValue())
                .numAdults(((Number) m.getOrDefault("numAdults", 0)).intValue())
                .numChildren(((Number) m.getOrDefault("numChildren", 0)).intValue())
                .maxOccupancy(((Number) m.getOrDefault("maxOccupancy", 0)).intValue())
                .checkInDate((String) m.get("checkInDate"))
                .checkOutDate((String) m.get("checkOutDate"))
                .mealType((String) m.get("mealType"))
                .rateKey((String) m.get("rateKey"))
                .totalRates((Map<String, Object>) m.get("totalRates"))
                .nightlyRates((Map<String, Object>) m.get("nightlyRates"))
                .charges((List<Map<String, Object>>) m.get("charges"))
                .cancellationPolicy((Map<String, Object>) m.get("cancellationPolicy"))
                .bedGroups((List<Map<String, Object>>) m.get("bedGroups"))
                .facilityData((List<Map<String, Object>>) m.get("facilityData"))
                .refundable(Boolean.TRUE.equals(m.get("refundable")))
                .smokingAllowed(Boolean.TRUE.equals(m.get("smokingAllowed")))
                .build();
    }

}
