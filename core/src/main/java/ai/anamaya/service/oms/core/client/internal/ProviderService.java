package ai.anamaya.service.oms.core.client.internal;

import ai.anamaya.service.oms.core.client.biztrip.BiztripAuthService;
import ai.anamaya.service.oms.core.client.internal.dto.request.ProviderFetchRequest;
import ai.anamaya.service.oms.core.client.internal.dto.request.ProviderSaveRequest;
import ai.anamaya.service.oms.core.client.internal.dto.response.ProviderResponse;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.exception.BiztripIntegrationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Service
public class ProviderService {

    private static final String FETCH_PATH = "/provider/admin/fetch";
    private static final String SAVE_PATH = "/provider/admin/save";

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    public ProviderService(@Qualifier("internalApiWebClient") WebClient webClient,
                            BiztripAuthService authService,
                            ObjectMapper mapper) {
        this.webClient = webClient;
        this.authService = authService;
        this.mapper = mapper;
    }

    public List<ProviderResponse> fetchProvider(CallerContext callerContext, ProviderFetchRequest request) {
        String token = authService.getAccessToken(callerContext.companyId());

        String rawResponse = call(() -> webClient.post()
                .uri(FETCH_PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block());

        JsonNode dataNode = extractDataNode(rawResponse);
        try {
            CollectionType listType = mapper.getTypeFactory()
                .constructCollectionType(List.class, ProviderResponse.class);
            return mapper.convertValue(dataNode, listType);
        } catch (Exception e) {
            log.error("Failed to parse provider fetch response: {}", rawResponse, e);
            throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Invalid response from internal service");
        }
    }

    public String saveProvider(CallerContext callerContext, ProviderSaveRequest request) {
        String token = authService.getAccessToken(callerContext.companyId());

        String rawResponse = call(() -> webClient.post()
                .uri(SAVE_PATH)
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
            log.warn("Provider not found: {}", e.getResponseBodyAsString());
            throw new BiztripIntegrationException(HttpStatus.NOT_FOUND, "Provider data not found");
        } catch (WebClientResponseException e) {
            log.error("Provider API error: {} - {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            HttpStatus status = e.getStatusCode().is4xxClientError() ? HttpStatus.BAD_REQUEST : HttpStatus.BAD_GATEWAY;
            throw new BiztripIntegrationException(status, "Provider request failed");
        } catch (WebClientRequestException e) {
            log.error("Provider connection failed", e);
            throw new BiztripIntegrationException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to reach internal service");
        }
    }

    private JsonNode extractDataNode(String rawResponse) {
        try {
            JsonNode root = mapper.readTree(rawResponse);
            boolean success = root.path("success").asBoolean(false);
            JsonNode dataNode = root.get("data");

            if (!success || dataNode == null || dataNode.isNull()) {
                log.error("Provider request returned unsuccessful response: {}", rawResponse);
                throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Provider request was not successful");
            }

            return dataNode;
        } catch (BiztripIntegrationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse provider response: {}", rawResponse, e);
            throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Invalid response from internal service");
        }
    }
}
