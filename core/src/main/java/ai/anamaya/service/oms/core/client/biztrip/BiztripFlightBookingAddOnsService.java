package ai.anamaya.service.oms.core.client.biztrip;


import ai.anamaya.service.oms.core.client.biztrip.dto.flight.response.BiztripFlightAddOnDataResponse;
import ai.anamaya.service.oms.core.client.biztrip.dto.flight.response.BiztripFlightAddOnsResponse;
import ai.anamaya.service.oms.core.client.biztrip.mapper.response.BiztripFlightAddOnsResponseMapper;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.FlightAddOnsRequest;
import ai.anamaya.service.oms.core.dto.response.FlightAddOnsResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class BiztripFlightBookingAddOnsService {

    private final WebClient webClient;
    private final BiztripAuthService authService;
    private final ObjectMapper mapper;
    private final BiztripFlightAddOnsResponseMapper responseMapper = new BiztripFlightAddOnsResponseMapper();

    public FlightAddOnsResponse getAddOns(CallerContext callerContext, FlightAddOnsRequest request) {
        try {
            String token = authService.getAccessToken(callerContext.companyId());

            String rawResponse = webClient.post()
                    .uri("/flight/booking/add-ons")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(15))
                    .block();

            log.info("Biztrip raw response: {}", rawResponse);

            JsonNode root = mapper.readTree(rawResponse);
            boolean success = root.path("success").asBoolean(false);
            JsonNode dataNode = root.get("data");
            BiztripFlightAddOnsResponse biztripResponse =
                mapper.treeToValue(dataNode, BiztripFlightAddOnsResponse.class);

            return responseMapper.map(success, biztripResponse);

        } catch (Exception e) {
            log.error("Search add ons to Biztrip failed", e);
            throw new RuntimeException("Search add ons failed: " + e.getMessage());
        }
    }

}
