package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.dto.response.BiztripTokenResponse;
import ai.anamaya.service.oms.core.entity.CompanyConfig;
import ai.anamaya.service.oms.core.repository.CompanyConfigRepository;
import lombok.extern.slf4j.Slf4j;
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

    private static final String REDIS_KEY = "biztrip:access_token:";

    private final WebClient webClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CompanyConfigRepository companyConfigRepository;

    public BiztripAuthService(@Qualifier("biztripWebClient") WebClient webClient,
                              RedisTemplate<String, Object> redisTemplate,
                              CompanyConfigRepository companyConfigRepository) {
        this.webClient = webClient;
        this.redisTemplate = redisTemplate;
        this.companyConfigRepository = companyConfigRepository;
    }

    public String getAccessToken(Long companyId) {
        String redisKey = REDIS_KEY + companyId;
        Object cachedToken = redisTemplate.opsForValue().get(redisKey);
        if (cachedToken != null) {
            log.info("Using cached BizTrip token");
            return cachedToken.toString();
        }

        String clientId = companyConfigRepository.findByCompanyIdAndCode(companyId, "BIZTRIP_CLIENT_ID")
                .map(CompanyConfig::getValueStr)
                .orElseThrow(() -> new RuntimeException("Missing BIZTRIP_CLIENT_ID for company " + companyId));

        String clientSecret = companyConfigRepository.findByCompanyIdAndCode(companyId, "BIZTRIP_CLIENT_SECRET")
                .map(CompanyConfig::getValueStr)
                .orElseThrow(() -> new RuntimeException("Missing BIZTRIP_CLIENT_SECRET for company " + companyId));
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
                    redisKey,
                    tokenResponse.getAccessToken(),
                    tokenResponse.getExpiredIn() / 1000,
                    TimeUnit.SECONDS
            );

            log.info("New BizTrip token fetched and cached ({}s)", tokenResponse.getExpiredIn());
            return tokenResponse.getAccessToken();

        } catch (WebClientResponseException e) {
            log.error("BizTrip Auth API error: {} - {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new RuntimeException("BizTrip auth failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error fetching BizTrip token", e);
            throw new RuntimeException("Unexpected BizTrip auth error: " + e.getMessage());
        }
    }
}
