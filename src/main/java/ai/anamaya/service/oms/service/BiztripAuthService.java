package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.response.BiztripTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BiztripAuthService {

    private static final String REDIS_KEY = "biztrip:access_token";

    private final WebClient webClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${external.biztrip-api.client-id}")
    private String clientId;

    @Value("${external.biztrip-api.client-secret}")
    private String clientSecret;

    public BiztripAuthService(@Qualifier("biztripWebClient") WebClient webClient,
                              RedisTemplate<String, Object> redisTemplate) {
        this.webClient = webClient;
        this.redisTemplate = redisTemplate;
    }

    public String getAccessToken() {
        Object cachedToken = redisTemplate.opsForValue().get(REDIS_KEY);
        if (cachedToken != null) {
            log.info("Using cached BizTrip token");
            return cachedToken.toString();
        }

        try {
            BiztripTokenResponse tokenResponse = webClient.post()
                    .uri("/oauth/accesstoken") // ✅ trailing slash
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                            "client_id", clientId,
                            "client_secret", clientSecret
                    ))
                    .retrieve()
                    .bodyToMono(BiztripTokenResponse.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
                throw new RuntimeException("No access token received from BizTrip");
            }

            redisTemplate.opsForValue().set(
                    REDIS_KEY,
                    tokenResponse.getAccessToken(),
                    tokenResponse.getExpiredIn() / 1000,
                    TimeUnit.SECONDS
            );

            log.info("New BizTrip token fetched and cached ({}s)", tokenResponse.getExpiredIn());
            return tokenResponse.getAccessToken();

        } catch (WebClientResponseException e) {
            log.error("BizTrip Auth API error: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("BizTrip auth failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching BizTrip token", e);
            throw new RuntimeException("Unexpected BizTrip auth error: " + e.getMessage());
        }
    }
}
