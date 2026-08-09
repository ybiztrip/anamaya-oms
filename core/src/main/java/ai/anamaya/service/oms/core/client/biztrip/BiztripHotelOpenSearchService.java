package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.UpdateHotelOpenSearchRequest;
import ai.anamaya.service.oms.core.dto.response.HotelOpenSearchResponse;
import ai.anamaya.service.oms.core.exception.BiztripIntegrationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@Service
public class BiztripHotelOpenSearchService {

    private static final String PATH = "/hotel/admin/opensearch/{id}";

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public HotelOpenSearchResponse getOpenSearch(CallerContext callerContext, String id) {
        String token = authService.getAccessToken(callerContext.companyId());

        String rawResponse = call(() -> biztripWebClient.get()
                .uri(PATH, id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(String.class)
                .block());

        return parseResponse(rawResponse);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public HotelOpenSearchResponse updateOpenSearch(CallerContext callerContext, String id, UpdateHotelOpenSearchRequest request) {
        String token = authService.getAccessToken(callerContext.companyId());

        String rawResponse = call(() -> biztripWebClient.put()
                .uri(PATH, id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block());

        return parseResponse(rawResponse);
    }

    private String call(Supplier<String> request) {
        try {
            return request.get();
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Biztrip hotel opensearch not found: {}", e.getResponseBodyAsString());
            throw new BiztripIntegrationException(HttpStatus.NOT_FOUND, "Hotel opensearch data not found");
        } catch (WebClientResponseException e) {
            log.error("Biztrip hotel opensearch API error: {} - {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            HttpStatus status = e.getStatusCode().is4xxClientError() ? HttpStatus.BAD_REQUEST : HttpStatus.BAD_GATEWAY;
            throw new BiztripIntegrationException(status, "Biztrip hotel opensearch request failed");
        } catch (WebClientRequestException e) {
            log.error("Biztrip hotel opensearch connection failed", e);
            throw new BiztripIntegrationException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to reach Biztrip service");
        }
    }

    private HotelOpenSearchResponse parseResponse(String rawResponse) {
        try {
            JsonNode root = mapper.readTree(rawResponse);
            boolean success = root.path("success").asBoolean(false);
            JsonNode dataNode = root.get("data");

            if (!success || dataNode == null || dataNode.isNull()) {
                log.error("Biztrip hotel opensearch returned unsuccessful response: {}", rawResponse);
                throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Biztrip hotel opensearch request was not successful");
            }

            return mapper.treeToValue(dataNode, HotelOpenSearchResponse.class);
        } catch (BiztripIntegrationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse Biztrip hotel opensearch response: {}", rawResponse, e);
            throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Invalid response from Biztrip service");
        }
    }
}
