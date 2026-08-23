package ai.anamaya.service.oms.core.client.internal;

import ai.anamaya.service.oms.core.client.biztrip.BiztripAuthService;
import ai.anamaya.service.oms.core.client.internal.dto.response.AccountResponse;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.exception.BiztripIntegrationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Service
public class AccountListService {

    private static final String PATH = "/account/admin/list";

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    public AccountListService(@Qualifier("internalApiWebClient") WebClient webClient,
                               BiztripAuthService authService,
                               ObjectMapper mapper) {
        this.webClient = webClient;
        this.authService = authService;
        this.mapper = mapper;
    }

    public List<AccountResponse> getAccountList(CallerContext callerContext) {
        String token = authService.getAccessToken(callerContext.companyId());

        String rawResponse = call(() -> webClient.get()
                .uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(String.class)
                .block());

        JsonNode dataNode = extractDataNode(rawResponse);
        try {
            CollectionType listType = mapper.getTypeFactory()
                .constructCollectionType(List.class, AccountResponse.class);
            return mapper.convertValue(dataNode, listType);
        } catch (Exception e) {
            log.error("Failed to parse account list response: {}", rawResponse, e);
            throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Invalid response from internal service");
        }
    }

    private String call(Supplier<String> request) {
        try {
            return request.get();
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Account list not found: {}", e.getResponseBodyAsString());
            throw new BiztripIntegrationException(HttpStatus.NOT_FOUND, "Account list not found");
        } catch (WebClientResponseException e) {
            log.error("Account list API error: {} - {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            HttpStatus status = e.getStatusCode().is4xxClientError() ? HttpStatus.BAD_REQUEST : HttpStatus.BAD_GATEWAY;
            throw new BiztripIntegrationException(status, "Account list request failed");
        } catch (WebClientRequestException e) {
            log.error("Account list connection failed", e);
            throw new BiztripIntegrationException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to reach internal service");
        }
    }

    private JsonNode extractDataNode(String rawResponse) {
        try {
            JsonNode root = mapper.readTree(rawResponse);
            boolean success = root.path("success").asBoolean(false);
            JsonNode dataNode = root.get("data");

            if (!success || dataNode == null || dataNode.isNull()) {
                log.error("Account list returned unsuccessful response: {}", rawResponse);
                throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Account list request was not successful");
            }

            return dataNode;
        } catch (BiztripIntegrationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse account list response: {}", rawResponse, e);
            throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Invalid response from internal service");
        }
    }
}
