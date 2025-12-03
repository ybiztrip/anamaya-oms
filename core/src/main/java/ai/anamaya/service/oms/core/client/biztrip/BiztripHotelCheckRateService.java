package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.dto.request.HotelRateCheckRequest;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.HotelRateCheckResponse;
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

@Slf4j
@Service
public class BiztripHotelCheckRateService {

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final JwtUtils jwtUtils;

    public BiztripHotelCheckRateService(@Qualifier("biztripWebClient") WebClient webClient,
                                        BiztripAuthService authService,
                                        JwtUtils jwtUtils) {
        this.webClient = webClient;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    public ApiResponse<HotelRateCheckResponse> checkHotelRate(HotelRateCheckRequest request) {
        try {
            Long companyId = jwtUtils.getCompanyIdFromToken();
            String accessToken = authService.getAccessToken(companyId);

            Map<String, Object> response = webClient.post()
                    .uri("/v2/hotel/rate/check")
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("BizTrip Hotel Rate Check API error: {}", body);
                                        return Mono.error(new RuntimeException("Failed to fetch hotel rate check data"));
                                    })
                    )
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(15))
                    .block();

            if (response == null || response.get("data") == null) {
                return ApiResponse.error("No hotel rate check data found");
            }

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            return ApiResponse.success(mapToHotelRateCheckResponse(data));

        } catch (WebClientResponseException e) {
            log.error("BizTrip API error: {} - {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            return ApiResponse.error("External API error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching hotel rate check", e);
            return ApiResponse.error("Unexpected error: " + e.getMessage());
        }
    }

    private HotelRateCheckResponse mapToHotelRateCheckResponse(Map<String, Object> data) {
        return HotelRateCheckResponse.builder()
                .rateStatus((String) data.get("rateStatus"))
                .propertyId((String) data.get("propertyId"))
                .providerRoomId((String) data.get("providerRoomId"))
                .roomId((String) data.get("roomId"))
                .roomName((String) data.get("roomName"))
                .roomType((String) data.get("roomType"))
                .checkInDate((String) data.get("checkInDate"))
                .checkOutDate((String) data.get("checkOutDate"))
                .numRooms((Integer) data.get("numRooms"))
                .numAdults((Integer) data.get("numAdults"))
                .numChildren((Integer) data.get("numChildren"))
                .mealType((String) data.get("mealType"))
                .totalRates((Map<String, Object>) data.get("totalRates"))
                .nightlyRates((Map<String, Object>) data.get("nightlyRates"))
                .charges((List<Map<String, Object>>) data.get("charges"))
                .cancellationPolicy((Map<String, Object>) data.get("cancellationPolicy"))
                .occupancyPricing((Map<String, Object>) data.get("occupancyPricing"))
                .refundable((Boolean) data.get("refundable"))
                .isRefundable((Boolean) data.get("isRefundable"))
                .build();
    }

}
