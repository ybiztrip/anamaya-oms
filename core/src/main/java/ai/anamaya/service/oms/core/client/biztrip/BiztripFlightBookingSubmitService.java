package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.BiztripSubmitResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.BiztripBookingSubmitMapper;
import ai.anamaya.service.oms.core.dto.request.booking.submit.BookingSubmitRequest;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingSubmitResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
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
    private final JwtUtils jwtUtils;
    private final ObjectMapper mapper;

    private final BiztripBookingSubmitMapper submitMapper = new BiztripBookingSubmitMapper();

    public BookingSubmitResponse submit(BookingSubmitRequest request) {
        try {
            Long companyId = jwtUtils.getCompanyIdFromToken();
            String token = authService.getAccessToken(companyId);

            logRequestAsCurl(request, token);

            String rawResponse = biztripWebClient.post()
                .uri("/flight/booking/submit")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.error("Biztrip raw response: {}", rawResponse);

            BiztripSubmitResponse biztripResponse =
                mapper.readValue(rawResponse, BiztripSubmitResponse.class);

            if (biztripResponse == null ||
                biztripResponse.getFlightBookingDetail() == null) {

                throw new RuntimeException(
                    "Biztrip did not return flightBookingDetail. Raw response: "
                        + rawResponse
                );
            }

            return submitMapper.map(biztripResponse);

        } catch (Exception e) {
            log.error("Submit booking to Biztrip failed", e);
            throw new RuntimeException("Booking submission failed: " + e.getMessage());
        }
    }

    private void logRequestAsCurl(BookingSubmitRequest request, String token) {
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
