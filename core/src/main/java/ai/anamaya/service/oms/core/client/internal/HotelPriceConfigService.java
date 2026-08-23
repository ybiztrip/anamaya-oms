package ai.anamaya.service.oms.core.client.internal;

import ai.anamaya.service.oms.core.client.biztrip.BiztripAuthService;
import ai.anamaya.service.oms.core.client.internal.dto.request.HotelPriceConfigSaveRequest;
import ai.anamaya.service.oms.core.client.internal.dto.response.HotelPriceConfigResponse;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.exception.BiztripIntegrationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.function.Supplier;

@Slf4j
@Service
public class HotelPriceConfigService {

    private static final String PATH = "/hotel/admin/price-config";

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    public HotelPriceConfigService(@Qualifier("internalApiWebClient") WebClient webClient,
                                    BiztripAuthService authService,
                                    ObjectMapper mapper) {
        this.webClient = webClient;
        this.authService = authService;
        this.mapper = mapper;
    }

    public HotelPriceConfigResponse getPriceConfig(CallerContext callerContext, String accountId) {
        String token = authService.getAccessToken(callerContext.companyId());

        String rawResponse = call(() -> webClient.get()
                .uri(uriBuilder -> uriBuilder.path(PATH)
                        .queryParam("accountId", accountId)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(String.class)
                .block());

        JsonNode dataNode = extractDataNode(rawResponse);
        try {
            return mapper.treeToValue(dataNode, HotelPriceConfigResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse hotel price-config response: {}", rawResponse, e);
            throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Invalid response from internal service");
        }
    }

    public String savePriceConfig(CallerContext callerContext, HotelPriceConfigSaveRequest request) {
        String token = authService.getAccessToken(callerContext.companyId());

        String rawResponse = call(() -> webClient.post()
                .uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block());

        JsonNode dataNode = extractDataNode(rawResponse);
        return dataNode.asText();
    }

    private String call(Supplier<String> request) {
        try {
            return request.get();
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Hotel price-config not found: {}", e.getResponseBodyAsString());
            throw new BiztripIntegrationException(HttpStatus.NOT_FOUND, "Hotel price-config data not found");
        } catch (WebClientResponseException e) {
            log.error("Hotel price-config API error: {} - {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            HttpStatus status = e.getStatusCode().is4xxClientError() ? HttpStatus.BAD_REQUEST : HttpStatus.BAD_GATEWAY;
            throw new BiztripIntegrationException(status, "Hotel price-config request failed");
        } catch (WebClientRequestException e) {
            log.error("Hotel price-config connection failed", e);
            throw new BiztripIntegrationException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to reach internal service");
        }
    }

    private JsonNode extractDataNode(String rawResponse) {
        try {
            JsonNode root = mapper.readTree(rawResponse);
            boolean success = root.path("success").asBoolean(false);
            JsonNode dataNode = root.get("data");

            if (!success || dataNode == null || dataNode.isNull()) {
                log.error("Hotel price-config returned unsuccessful response: {}", rawResponse);
                throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Hotel price-config request was not successful");
            }

            return dataNode;
        } catch (BiztripIntegrationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse hotel price-config response: {}", rawResponse, e);
            throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Invalid response from internal service");
        }
    }
}
