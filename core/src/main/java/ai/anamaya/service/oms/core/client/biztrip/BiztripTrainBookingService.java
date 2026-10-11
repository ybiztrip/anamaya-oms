package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.train.BiztripTrainBookingData;
import ai.anamaya.service.oms.core.context.CallerContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.Map;

// Uses callerContext (not the JWT) so consumer and worker can call it too
@Slf4j
@Service
public class BiztripTrainBookingService {

    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    public BiztripTrainBookingService(@Qualifier("biztripWebClient") WebClient webClient,
                                      BiztripAuthService authService,
                                      ObjectMapper mapper) {
        this.webClient = webClient;
        this.authService = authService;
        this.mapper = mapper;
    }

    public BiztripTrainBookingData book(CallerContext callerContext, Map<String, Object> body) {
        JsonNode response = post(callerContext, "/train/book", body);
        BiztripTrainBookingData data = read(unwrap(response), BiztripTrainBookingData.class);
        if (data == null || data.getId() == null) {
            throw new IllegalStateException("Train booking failed: " + response);
        }
        return data;
    }

    public BiztripTrainBookingData detail(CallerContext callerContext, String bookingReference) {
        String token = authService.getAccessToken(callerContext.companyId());
        try {
            String raw = webClient.get()
                .uri(uri -> uri.path("/train/booking/detail").queryParam("id", bookingReference).build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(TIMEOUT)
                .block();
            log.debug("Biztrip /train/booking/detail response: {}", raw);
            return read(unwrap(mapper.readTree(raw)), BiztripTrainBookingData.class);
        } catch (WebClientResponseException e) {
            throw new IllegalStateException("Biztrip /train/booking/detail failed: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new IllegalStateException("Biztrip /train/booking/detail failed: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> seatMap(CallerContext callerContext, Map<String, Object> body) {
        return mapper.convertValue(unwrap(post(callerContext, "/train/seat-map", body)), new TypeReference<>() {});
    }

    public void setSeat(CallerContext callerContext, Map<String, Object> body) {
        JsonNode response = post(callerContext, "/train/set-seat", body);
        if (!response.path("isSuccess").asBoolean(false) && !response.path("success").asBoolean(false)) {
            throw new IllegalStateException("Set train seat failed: " + response);
        }
    }

    // {"TicketConfirmStatus": "OK", "Error": ""}
    public JsonNode issue(CallerContext callerContext, String bookingReference) {
        return unwrap(post(callerContext, "/train/issue", Map.of("id", bookingReference, "CallbackUri", "")));
    }

    private JsonNode post(CallerContext callerContext, String uri, Object body) {
        String token = authService.getAccessToken(callerContext.companyId());
        try {
            String raw = webClient.post()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(TIMEOUT)
                .block();
            log.debug("Biztrip {} response: {}", uri, raw);
            return mapper.readTree(raw);
        } catch (WebClientResponseException e) {
            throw new IllegalStateException("Biztrip " + uri + " failed: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new IllegalStateException("Biztrip " + uri + " failed: " + e.getMessage(), e);
        }
    }

    // Some train endpoints wrap the payload in {"success", "data"}, others return it bare
    private JsonNode unwrap(JsonNode node) {
        JsonNode data = node.has("data") ? node.get("data") : node.get("Data");
        return data != null && !data.isNull() ? data : node;
    }

    private <T> T read(JsonNode node, Class<T> type) {
        try {
            return mapper.treeToValue(node, type);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot parse Biztrip train response: " + node, e);
        }
    }
}
