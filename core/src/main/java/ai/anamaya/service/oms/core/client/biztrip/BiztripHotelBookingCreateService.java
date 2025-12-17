package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request.BiztripHotelBookingCreateRequest;
import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelBookingCreateResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.request.BiztripHotelBookingSubmitRequestMapper;
import ai.anamaya.service.oms.core.client.biztrip.mapper.response.BiztripHotelBookingCreateResponseMapper;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingCreateRequest;
import ai.anamaya.service.oms.core.dto.response.booking.hotel.HotelBookingCreateResponse;
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
public class BiztripHotelBookingCreateService {

    private final WebClient biztripWebClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;

    private final BiztripHotelBookingSubmitRequestMapper submitRequestMapper =
        new BiztripHotelBookingSubmitRequestMapper();

    private final BiztripHotelBookingCreateResponseMapper submitResponseMapper =
        new BiztripHotelBookingCreateResponseMapper();

    public HotelBookingCreateResponse create(CallerContext callerContext, HotelBookingCreateRequest request) {
        try {
            String token = authService.getAccessToken(callerContext.companyId());

            BiztripHotelBookingCreateRequest biztripReq =
                submitRequestMapper.map(request);

            logRequestAsCurl(biztripReq, token);

            String rawResponse = biztripWebClient.post()
                .uri("/v2/hotel/booking/create")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(biztripReq)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            log.error("Biztrip Hotel raw response: {}", rawResponse);

            JsonNode root = mapper.readTree(rawResponse);
            boolean success = root.path("success").asBoolean(false);

            JsonNode dataNode = root.get("data");
            BiztripHotelBookingCreateResponse biztripResponse =
                mapper.treeToValue(dataNode, BiztripHotelBookingCreateResponse.class);

            return submitResponseMapper.map(success, biztripResponse);

        } catch (Exception e) {
            log.error("Submit hotel booking to Biztrip failed", e);
            throw new RuntimeException("Hotel booking submission failed: " + e.getMessage());
        }
    }

    private void logRequestAsCurl(
        BiztripHotelBookingCreateRequest request,
        String token
    ) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(request);

            String curl = "curl -X POST \"" +
                "https://dev-affiliate.biztrip.id/v2/hotel/booking/create" +
                "\" \\\n" +
                "-H \"Authorization: " + token + "\" \\\n" +
                "-H \"Content-Type: application/json\" \\\n" +
                "-d '" + json + "'";

            log.error("Biztrip Hotel Request as cURL:\n{}", curl);

        } catch (Exception ex) {
            log.error("Failed to log Biztrip hotel cURL request", ex);
        }
    }
}
