package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.PropertyMappingRequest;
import ai.anamaya.service.oms.core.dto.response.HotelPropertyMappingResponse;
import ai.anamaya.service.oms.core.dto.response.HotelPropertyMappingUpdateResponse;
import ai.anamaya.service.oms.core.exception.BiztripIntegrationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
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

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@Service
public class BiztripHotelPropertyMappingService {

    private static final String PATH = "/hotel/admin/property-mapping/{id}";

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    public List<HotelPropertyMappingResponse> getPropertyMapping(CallerContext callerContext, String id) {
        String token = authService.getAccessToken(callerContext.companyId());

        String rawResponse = call(() -> biztripWebClient.get()
                .uri(PATH, id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(String.class)
                .block());

        JsonNode dataNode = extractDataNode(rawResponse);
        try {
            CollectionType listType = mapper.getTypeFactory()
                .constructCollectionType(List.class, HotelPropertyMappingResponse.class);
            return mapper.convertValue(dataNode, listType);
        } catch (Exception e) {
            log.error("Failed to parse Biztrip hotel property-mapping response: {}", rawResponse, e);
            throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Invalid response from Biztrip service");
        }
    }

    public HotelPropertyMappingUpdateResponse updatePropertyMapping(CallerContext callerContext, String id, PropertyMappingRequest request) {
        String token = authService.getAccessToken(callerContext.companyId());

        String rawResponse = call(() -> biztripWebClient.post()
                .uri(PATH, id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block());

        JsonNode dataNode = extractDataNode(rawResponse);
        try {
            return mapper.treeToValue(dataNode, HotelPropertyMappingUpdateResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse Biztrip hotel property-mapping response: {}", rawResponse, e);
            throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Invalid response from Biztrip service");
        }
    }

    private String call(Supplier<String> request) {
        try {
            return request.get();
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Biztrip hotel property-mapping not found: {}", e.getResponseBodyAsString());
            throw new BiztripIntegrationException(HttpStatus.NOT_FOUND, "Hotel property mapping data not found");
        } catch (WebClientResponseException e) {
            log.error("Biztrip hotel property-mapping API error: {} - {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            HttpStatus status = e.getStatusCode().is4xxClientError() ? HttpStatus.BAD_REQUEST : HttpStatus.BAD_GATEWAY;
            throw new BiztripIntegrationException(status, "Biztrip hotel property-mapping request failed");
        } catch (WebClientRequestException e) {
            log.error("Biztrip hotel property-mapping connection failed", e);
            throw new BiztripIntegrationException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to reach Biztrip service");
        }
    }

    private JsonNode extractDataNode(String rawResponse) {
        try {
            JsonNode root = mapper.readTree(rawResponse);
            boolean success = root.path("success").asBoolean(false);
            JsonNode dataNode = root.get("data");

            if (!success || dataNode == null || dataNode.isNull()) {
                log.error("Biztrip hotel property-mapping returned unsuccessful response: {}", rawResponse);
                throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Biztrip hotel property-mapping request was not successful");
            }

            return dataNode;
        } catch (BiztripIntegrationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse Biztrip hotel property-mapping response: {}", rawResponse, e);
            throw new BiztripIntegrationException(HttpStatus.BAD_GATEWAY, "Invalid response from Biztrip service");
        }
    }
}
