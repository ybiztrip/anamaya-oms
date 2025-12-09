package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.request.BiztripBookingCheckFullStatusRequest;
import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.BiztripBaseResponse;
import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.BiztripCheckFullStatusResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.request.BiztripFlightBookingCheckFullStatusRequestMapper;
import ai.anamaya.service.oms.core.client.biztrip.mapper.response.BiztripFlightBookingCheckFullStatusResponseMapper;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.booking.status.FlightBookingStatusCheckRequest;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingFlightSubmitResponse;
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
public class BiztripFlightBookingCheckStatusService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    private final BiztripFlightBookingCheckFullStatusRequestMapper requestMapper = new BiztripFlightBookingCheckFullStatusRequestMapper();
    private final BiztripFlightBookingCheckFullStatusResponseMapper submitResponseMapper = new BiztripFlightBookingCheckFullStatusResponseMapper();


    public BookingFlightSubmitResponse checkStatus(CallerContext callerContext, FlightBookingStatusCheckRequest request) {
        try {
            String token = authService.getAccessToken(callerContext.companyId());
            BiztripBookingCheckFullStatusRequest biztripReq = requestMapper.map(request);

            String rawResponse = biztripWebClient.post()
                .uri("/flight/booking/full-status")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(biztripReq)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.debug("Biztrip raw response: {}", rawResponse);

            BiztripBaseResponse<BiztripCheckFullStatusResponse> biztripResponse =
                mapper.readValue(
                    rawResponse,
                    new TypeReference<BiztripBaseResponse<BiztripCheckFullStatusResponse>>() {}
                );

            if (biztripResponse == null
                || biztripResponse.getData() == null
                || biztripResponse.getData().getBookingStatusResult().isEmpty()
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

}
