package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.request.BiztripBookingSubmitRequest;
import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.BiztripSubmitResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.request.BiztripBookingSubmitRequestMapper;
import ai.anamaya.service.oms.core.client.biztrip.mapper.response.BiztripBookingSubmitResponseMapper;
import ai.anamaya.service.oms.core.dto.request.booking.status.BookingStatusCheckRequest;
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
public class BiztripFlightBookingCheckStatusService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final JwtUtils jwtUtils;
    private final ObjectMapper mapper;

    private final BiztripBookingSubmitResponseMapper submitResponseMapper = new BiztripBookingSubmitResponseMapper();

    public BookingSubmitResponse checkStatus(BookingStatusCheckRequest request) {
        try {
            Long companyId = jwtUtils.getCompanyIdFromToken();
            String token = authService.getAccessToken(companyId);

            String rawResponse = biztripWebClient.post()
                .uri("/flight/booking/full-status")
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

            return submitResponseMapper.map(biztripResponse);

        } catch (Exception e) {
            log.error("Submit booking to Biztrip failed", e);
            throw new RuntimeException("Booking submission failed: " + e.getMessage());
        }
    }

}
