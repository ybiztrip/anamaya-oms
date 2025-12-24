package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request.BiztripHotelDiscoveryRequest;
import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelDiscoveryResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.request.BiztripHotelDiscoveryRequestMapper;
import ai.anamaya.service.oms.core.client.biztrip.mapper.response.BiztripHotelDiscoveryResponseMapper;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.HotelDiscoveryRequest;
import ai.anamaya.service.oms.core.dto.response.HotelDiscoveryResponse;
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
public class BiztripHotelDiscoveryService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    private final BiztripHotelDiscoveryRequestMapper requestMapper =
        new BiztripHotelDiscoveryRequestMapper();

    private final BiztripHotelDiscoveryResponseMapper responseMapper =
        new BiztripHotelDiscoveryResponseMapper();

    public HotelDiscoveryResponse discovery(CallerContext callerContext, HotelDiscoveryRequest request) {
        try {
            String token = authService.getAccessToken(callerContext.companyId());

            BiztripHotelDiscoveryRequest biztripReq = requestMapper.map(request);
            logRequestAsCurl(biztripReq, token);

            String rawResponse = biztripWebClient.post()
                .uri("/hotel/v1/list")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(biztripReq)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info("Biztrip Hotel discovery raw response: {}", rawResponse);

            JsonNode root = mapper.readTree(rawResponse);

            BiztripHotelDiscoveryResponse biztripResponse =
                mapper.treeToValue(root, BiztripHotelDiscoveryResponse.class);

            return responseMapper.map(biztripResponse);

        } catch (Exception e) {
            log.error("Get geo hotel discovery failed", e);
            throw new RuntimeException("Get geo hotel failed: " + e.getMessage());
        }
    }

    private void logRequestAsCurl(BiztripHotelDiscoveryRequest request, String token) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(request);

            String curl = "curl -X POST \"" +
                "https://dev-affiliate.biztrip.id/hotel/v1/list" +
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
