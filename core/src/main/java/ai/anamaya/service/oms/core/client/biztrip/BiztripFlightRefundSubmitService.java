package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.refund.request.BiztripFlightRefundSubmitRequest;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.RefundProviderRequest;
import ai.anamaya.service.oms.core.dto.response.RefundProviderResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class BiztripFlightRefundSubmitService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    public RefundProviderResponse submit(CallerContext callerContext, RefundProviderRequest request) {
        try {
            String token = authService.getAccessToken(callerContext.companyId());

            BiztripFlightRefundSubmitRequest biztripReq = BiztripFlightRefundSubmitRequest.builder()
                .partnerBookingId(request.getPartnerBookingId())
                .bookingId(request.getBookingId())
                .build();

            logRequestAsCurl(biztripReq, token);

            String rawResponse = biztripWebClient.post()
                .uri("/flight-refund/submit")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(biztripReq)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info("Biztrip flight refund raw response: {}", rawResponse);

            JsonNode root = mapper.readTree(rawResponse);
            boolean success = root.path("success").asBoolean(false);
            String errorMessage = root.path("errorMessage").asText(null);

            if (!success) {
                throw new RuntimeException("Biztrip flight refund rejected: " + errorMessage);
            }

            return RefundProviderResponse.builder()
                .success(true)
                .errorMessage(errorMessage)
                .build();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Submit flight refund to Biztrip failed", e);
            throw new RuntimeException("Flight refund submission failed: " + e.getMessage());
        }
    }

    private void logRequestAsCurl(BiztripFlightRefundSubmitRequest request, String token) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(request);

            String curl = "curl -X POST \"" +
                "https://dev-affiliate.biztrip.id/flight-refund/submit" +
                "\" \\\n" +
                "-H \"Authorization: " + token + "\" \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '" + json + "'";

            log.info("Biztrip Flight Refund Request as cURL:\n{}", curl);

        } catch (Exception ex) {
            log.error("Failed to log Biztrip flight refund cURL request", ex);
        }
    }
}
