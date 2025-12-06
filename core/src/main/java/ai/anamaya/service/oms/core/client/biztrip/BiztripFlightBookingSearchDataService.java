package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.request.BiztripBookingSearchDataRequest;
import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.BiztripBaseResponse;
import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.BiztripDataResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.request.BiztripBookingSearchDataRequestMapper;
import ai.anamaya.service.oms.core.client.biztrip.mapper.response.BiztripBookingSearchDataResponseMapper;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.booking.submit.BookingSearchDataRequest;
import ai.anamaya.service.oms.core.dto.response.booking.data.BookingDataResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class BiztripFlightBookingSearchDataService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    private final BiztripBookingSearchDataRequestMapper submitRequestMapper = new BiztripBookingSearchDataRequestMapper();
    private final BiztripBookingSearchDataResponseMapper submitResponseMapper = new BiztripBookingSearchDataResponseMapper();

    public List<BookingDataResponse> search(CallerContext callerContext, BookingSearchDataRequest request) {
        try {
            String token = authService.getAccessToken(callerContext.companyId());

            BiztripBookingSearchDataRequest biztripReq = submitRequestMapper.map(request);
            logRequestAsCurl(biztripReq, token);

            String rawResponse = biztripWebClient.post()
                .uri("/flight/booking/data")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(biztripReq)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.error("Biztrip raw response: {}", rawResponse);

            BiztripBaseResponse<List<BiztripDataResponse>> biztripResponse =
                mapper.readValue(
                    rawResponse,
                    new TypeReference<BiztripBaseResponse<List<BiztripDataResponse>>>() {}
                );

            if (biztripResponse == null
                || biztripResponse.getData() == null
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

    private void logRequestAsCurl(BiztripBookingSearchDataRequest request, String token) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(request);

            String curl = "curl -X POST \"" +
                "https://dev-affiliate.biztrip.id/flight/booking/data" +
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
