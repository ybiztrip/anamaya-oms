package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.FileFetchRequest;
import ai.anamaya.service.oms.core.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BiztripFileFetchService {

    private final WebClient webClient;
    private final BiztripAuthService authService;

    public byte[] fetch(CallerContext callerContext, FileFetchRequest request) {
        try {
            Long companyId = callerContext.companyId();
            String accessToken = authService.getAccessToken(companyId);

            byte[] response = webClient.post()
                .uri("/file/fetch")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_PDF) // ✅ FIXED
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(body -> {
                            log.error("File Fetch API error: {}", body);
                            return Mono.error(new RuntimeException("Failed to fetch PDF"));
                        })
                )
                .bodyToMono(byte[].class)
                .block();

            if (response == null || response.length == 0) {
                throw new RuntimeException("Empty PDF response");
            }

            return response;

        } catch (Exception e) {
            log.error("Unexpected error fetching PDF", e);
            throw new RuntimeException("Failed to fetch PDF", e);
        }
    }

}
