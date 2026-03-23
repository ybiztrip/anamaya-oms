package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.BiztripApiResponse;
import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripPaymentCCResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.response.BiztripPaymentCCResponseMapper;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.PaymentCCListRequest;
import ai.anamaya.service.oms.core.dto.response.PaymentCCResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
@Slf4j
@RequiredArgsConstructor
@Service
public class BiztripPaymentGetCCService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper objectMapper;
    private final BiztripPaymentCCResponseMapper responseMapper = new BiztripPaymentCCResponseMapper();

    public List<PaymentCCResponse> getCC(CallerContext callerContext, PaymentCCListRequest request) {
        String token = authService.getAccessToken(callerContext.companyId());

        try {
            String rawResponse = biztripWebClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/credit-cards");

                    if (request.getEmail() != null) {
                        uriBuilder.queryParam("email", request.getEmail());
                    }

                    return uriBuilder.build();
                })
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.debug("Biztrip CC raw response: {}", rawResponse);

            BiztripApiResponse<List<BiztripPaymentCCResponse>> response =
                objectMapper.readValue(
                    rawResponse,
                    new TypeReference<BiztripApiResponse<List<BiztripPaymentCCResponse>>>() {}
                );

            // handle unsuccessful response
            if (response == null || !response.isSuccess()) {
                log.error("Biztrip CC API returned unsuccessful response: {}", rawResponse);
                return Collections.emptyList();
            }

            List<BiztripPaymentCCResponse> data = response.getData();

            if (data == null || data.isEmpty()) {
                return Collections.emptyList();
            }

            return data.stream()
                .map(responseMapper::mapToResponse)
                .toList();

        } catch (Exception e) {
            log.error("Get payment cc failed", e);
            throw new RuntimeException("Get payment cc failed: " + e.getMessage(), e);
        }
    }
}
