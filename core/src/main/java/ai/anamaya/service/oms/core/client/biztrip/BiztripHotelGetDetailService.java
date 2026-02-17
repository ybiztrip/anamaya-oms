package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request.BiztripHotelBookingDetailRequest;
import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelBookingDetailResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.request.BiztripHotelBookingDetailRequestMapper;
import ai.anamaya.service.oms.core.client.biztrip.mapper.response.BiztripHotelBookingDetailResponseMapper;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingGetDetailRequest;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingDetailResponse;
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
public class BiztripHotelGetDetailService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    private final BiztripHotelBookingDetailRequestMapper requestMapper =
        new BiztripHotelBookingDetailRequestMapper();

    private final BiztripHotelBookingDetailResponseMapper responseMapper =
        new BiztripHotelBookingDetailResponseMapper();

    public HotelBookingDetailResponse getDetail(CallerContext callerContext, HotelBookingGetDetailRequest request) {
        try {
            String token = authService.getAccessToken(callerContext.companyId());

            BiztripHotelBookingDetailRequest biztripReq =
                requestMapper.map(request);

            logRequestAsCurl(biztripReq, token);

            String rawResponse = biztripWebClient.post()
                .uri("/v2/hotel/booking/detail")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(biztripReq)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info("Biztrip Hotel RateCheck raw response: {}", rawResponse);

            JsonNode root = mapper.readTree(rawResponse);
            boolean success = root.path("success").asBoolean(false);

            BiztripHotelBookingDetailResponse biztripResponse =
                mapper.treeToValue(root.get("data"), BiztripHotelBookingDetailResponse.class);

            return responseMapper.map(success, biztripResponse);

        } catch (Exception e) {
            log.error("Hotel rate check failed", e);
            throw new RuntimeException("Hotel rate check failed: " + e.getMessage());
        }
    }

    private void logRequestAsCurl(
        BiztripHotelBookingDetailRequest request,
        String token
    ) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(request);

            String curl = "curl -X POST \"" +
                "https://dev-affiliate.biztrip.id/v2/hotel/booking/detail" +
                "\" \\\n" +
                "-H \"Authorization: " + token + "\" \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '" + json + "'";

            log.info("Biztrip RateCheck Request as cURL:\n{}", curl);
        } catch (Exception e) {
            log.error("Failed to log rate check cURL", e);
        }
    }
}
