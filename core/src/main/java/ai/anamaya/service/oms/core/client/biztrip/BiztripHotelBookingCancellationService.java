package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.refund.request.BiztripHotelBookingCancellationRequest;
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
public class BiztripHotelBookingCancellationService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    public RefundProviderResponse submitCancellation(CallerContext callerContext, RefundProviderRequest request) {
        try {
            String token = authService.getAccessToken(callerContext.companyId());

            BiztripHotelBookingCancellationRequest biztripReq = BiztripHotelBookingCancellationRequest.builder()
                .partnerBookingId(request.getPartnerBookingId())
                .bookingId(request.getBookingId())
                .cancellationReason(request.getCancellationReason())
                .build();

            logRequestAsCurl(biztripReq, token);

            String rawResponse = biztripWebClient.post()
                .uri("/hotel/booking/submit-cancellation")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(biztripReq)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info("Biztrip hotel cancellation raw response: {}", rawResponse);

            JsonNode root = mapper.readTree(rawResponse);
            boolean success = root.path("success").asBoolean(false);
            String errorMessage = root.path("errorMessage").asText(null);

            if (!success) {
                throw new RuntimeException("Biztrip hotel cancellation rejected: " + errorMessage);
            }

            return RefundProviderResponse.builder()
                .success(true)
                .errorMessage(errorMessage)
                .build();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Submit hotel cancellation to Biztrip failed", e);
            throw new RuntimeException("Hotel cancellation submission failed: " + e.getMessage());
        }
    }

    private void logRequestAsCurl(BiztripHotelBookingCancellationRequest request, String token) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(request);

            String curl = "curl -X POST \"" +
                "https://dev-affiliate.biztrip.id/hotel/booking/submit-cancellation" +
                "\" \\\n" +
                "-H \"Authorization: " + token + "\" \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '" + json + "'";

            log.info("Biztrip Hotel Cancellation Request as cURL:\n{}", curl);

        } catch (Exception ex) {
            log.error("Failed to log Biztrip hotel cancellation cURL request", ex);
        }
    }
}
