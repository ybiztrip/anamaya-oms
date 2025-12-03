package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.request.BiztripBookingPaymentRequest;
import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.BiztripBaseResponse;
import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.BiztripBookingPaymentConfirmDirectResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.request.BiztripBookingPaymentRequestMapper;
import ai.anamaya.service.oms.core.client.biztrip.mapper.response.BiztripBookingPaymentConfirmDirectResponseMapper;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.booking.payment.BookingPaymentRequest;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingSubmitResponse;
import com.fasterxml.jackson.core.type.TypeReference;
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
public class BiztripFlightBookingPaymentService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    private final BiztripBookingPaymentRequestMapper submitRequestMapper = new BiztripBookingPaymentRequestMapper();
    private final BiztripBookingPaymentConfirmDirectResponseMapper submitResponseMapper = new BiztripBookingPaymentConfirmDirectResponseMapper();

    public BookingSubmitResponse payment(CallerContext callerContext, BookingPaymentRequest request) {
        try {
            String token = authService.getAccessToken(callerContext.companyId());

            BiztripBookingPaymentRequest biztripReq = submitRequestMapper.map(request);
            logRequestAsCurl(biztripReq, token);

            String rawResponse = biztripWebClient.post()
                .uri("/flight/payment/confirm/direct")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(biztripReq)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.debug("Biztrip raw response: {}", rawResponse);

            BiztripBaseResponse<BiztripBookingPaymentConfirmDirectResponse> biztripResponse =
                mapper.readValue(
                    rawResponse,
                    new TypeReference<BiztripBaseResponse<BiztripBookingPaymentConfirmDirectResponse>>() {}
                );

            if (biztripResponse == null || biztripResponse.getData() == null
            ) {
                throw new RuntimeException(
                    "Biztrip response data is null. Raw response: " + rawResponse
                );
            }

            return submitResponseMapper.map(biztripResponse.getData());

        } catch (Exception e) {
            log.error("Submit booking to Biztrip failed", e);
            throw new RuntimeException("Booking submission failed: " + e.getMessage());
        }
    }

    private void logRequestAsCurl(BiztripBookingPaymentRequest request, String token) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(request);

            String curl = "curl -X POST \"" +
                "https://dev-affiliate.biztrip.id/flight/payment/confirm/direct" +
                "\" \\\n" +
                "-H \"Authorization: " + token + "\" \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '" + json + "'";

            log.error("Biztrip Request as cURL:\n{}", curl);

        } catch (Exception ex) {
            log.error("Failed to log cURL request", ex);
        }
    }

}
