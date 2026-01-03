package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.request.BiztripBookingSubmitRequest;
import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.BiztripSubmitResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.request.BiztripFlightBookingSubmitRequestMapper;
import ai.anamaya.service.oms.core.client.biztrip.mapper.response.BiztripFlightBookingSubmitResponseMapper;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.booking.submit.FlightBookingSubmitRequest;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingFlightSubmitResponse;
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
public class BiztripFlightBookingSubmitService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    private final BiztripFlightBookingSubmitRequestMapper submitRequestMapper = new BiztripFlightBookingSubmitRequestMapper();
    private final BiztripFlightBookingSubmitResponseMapper submitResponseMapper = new BiztripFlightBookingSubmitResponseMapper();

    public BookingFlightSubmitResponse submit(CallerContext callerContext, FlightBookingSubmitRequest request) {
        try {
            String token = authService.getAccessToken(callerContext.companyId());

            BiztripBookingSubmitRequest biztripReq = submitRequestMapper.map(request);
            logRequestAsCurl(biztripReq, token);

            String rawResponse = biztripWebClient.post()
                .uri("/flight/booking/submit")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(biztripReq)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.info("Biztrip raw response: {}", rawResponse);

            JsonNode root = mapper.readTree(rawResponse);
            boolean success = root.path("success").asBoolean(false);
            JsonNode dataNode = root.get("data");
            BiztripSubmitResponse biztripResponse =
                mapper.treeToValue(dataNode, BiztripSubmitResponse.class);

            return submitResponseMapper.map(success, biztripResponse);

        } catch (Exception e) {
            log.error("Submit booking to Biztrip failed", e);
            throw new RuntimeException("Booking submission failed: " + e.getMessage());
        }
    }

    private void logRequestAsCurl(BiztripBookingSubmitRequest request, String token) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(request);

            String curl = "curl -X POST \"" +
                "https://dev-affiliate.biztrip.id/flight/booking/submit" +
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
