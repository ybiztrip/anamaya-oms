package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request.BiztripHotelBookingCheckRateRequest;
import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelRateCheckResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.request.BiztripHotelBookingCheckRateRequestMapper;
import ai.anamaya.service.oms.core.client.biztrip.mapper.response.BiztripHotelBookingCheckRateResponseMapper;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingCheckRateRequest;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCheckRateResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class BiztripHotelBookingCheckRateService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    private final BiztripHotelBookingCheckRateRequestMapper requestMapper =
        new BiztripHotelBookingCheckRateRequestMapper();

    private final BiztripHotelBookingCheckRateResponseMapper responseMapper =
        new BiztripHotelBookingCheckRateResponseMapper();

    public HotelBookingCheckRateResponse checkRate(CallerContext callerContext, HotelBookingCheckRateRequest request) {
        try {
            String token = authService.getAccessToken(callerContext.companyId());

            BiztripHotelBookingCheckRateRequest biztripReq =
                requestMapper.map(request);

            logRequestAsCurl(biztripReq, token);

            String rawResponse = biztripWebClient.post()
                .uri("/v2/hotel/rate/check")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(biztripReq)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info("Biztrip Hotel RateCheck raw response: {}", rawResponse);

            JsonNode root = mapper.readTree(rawResponse);
            boolean success = root.path("success").asBoolean(false);

            BiztripHotelRateCheckResponse biztripResponse =
                mapper.treeToValue(root.get("data"), BiztripHotelRateCheckResponse.class);

            return responseMapper.map(success, biztripResponse);

        } catch (Exception e) {
            log.error("Hotel rate check failed", e);
            throw new RuntimeException("Hotel rate check failed: " + e.getMessage());
        }
    }

    private void logRequestAsCurl(
        BiztripHotelBookingCheckRateRequest request,
        String token
    ) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(request);

            String curl = "curl -X POST \"" +
                "https://dev-affiliate.biztrip.id/v2/hotel/rate/check" +
                "\" \\\n" +
                "-H \"Authorization: " + token + "\" \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '" + json + "'";

            log.error("Biztrip RateCheck Request as cURL:\n{}", curl);
        } catch (Exception e) {
            log.error("Failed to log rate check cURL", e);
        }
    }
}
