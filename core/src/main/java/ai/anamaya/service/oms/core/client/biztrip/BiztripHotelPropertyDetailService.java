package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request.BiztripHotelPropertyDetailRequest;
import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelPropertyDetailResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.request.BiztripHotelPropertyDetailRequestMapper;
import ai.anamaya.service.oms.core.client.biztrip.mapper.response.BiztripHotelPropertyDetailResponseMapper;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.HotelPropertyDetailRequest;
import ai.anamaya.service.oms.core.dto.response.HotelDiscoveryResponse;
import ai.anamaya.service.oms.core.dto.response.HotelPropertyDetailResponse;
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
public class BiztripHotelPropertyDetailService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    private final BiztripHotelPropertyDetailRequestMapper requestMapper =
        new BiztripHotelPropertyDetailRequestMapper();

    private final BiztripHotelPropertyDetailResponseMapper responseMapper =
        new BiztripHotelPropertyDetailResponseMapper();

    public HotelPropertyDetailResponse propertyDetails(CallerContext callerContext, HotelPropertyDetailRequest request) {
        try {
            String token = authService.getAccessToken(callerContext.companyId());

            BiztripHotelPropertyDetailRequest biztripReq = requestMapper.map(request);
            logRequestAsCurl(biztripReq, token);

            String rawResponse = biztripWebClient.post()
                .uri("/hotel/property-detail")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(biztripReq)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info("Biztrip Hotel property detail raw response: {}", rawResponse);

            JsonNode root = mapper.readTree(rawResponse);

            BiztripHotelPropertyDetailResponse biztripResponse =
                mapper.treeToValue(root, BiztripHotelPropertyDetailResponse.class);

            return responseMapper.map(biztripResponse);

        } catch (Exception e) {
            log.error("Get geo hotel property detail failed", e);
            throw new RuntimeException("Get hotel property detail failed: " + e.getMessage());
        }
    }

    private void logRequestAsCurl(BiztripHotelPropertyDetailRequest request, String token) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(request);

            String curl = "curl -X POST \"" +
                "https://dev-affiliate.biztrip.id/hotel/property-detail" +
                "\" \\\n" +
                "-H \"Authorization: " + token + "\" \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '" + json + "'";

            log.error("Biztrip Request as cURL:\n{}", curl);

        } catch (Exception ex) {
            log.error("Failed to log cURL request", ex);
        }
    }

}
