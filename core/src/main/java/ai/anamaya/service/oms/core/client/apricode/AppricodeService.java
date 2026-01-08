package ai.anamaya.service.oms.core.client.apricode;

import ai.anamaya.service.oms.core.client.apricode.dto.request.AppricodeApprovalRequestRequest;
import ai.anamaya.service.oms.core.client.apricode.dto.request.AppricodeApprovalResponseRequest;
import ai.anamaya.service.oms.core.client.apricode.mapper.ApricodeApprovalRequestMapper;
import ai.anamaya.service.oms.core.client.apricode.mapper.ApricodeApprovalResponseMapper;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppricodeService {

    private final WebClient.Builder webClientBuilder;
    private final AppricodeProperties properties;
    private final ApricodeApprovalRequestMapper requestApprovalRequestMapper = new ApricodeApprovalRequestMapper();
    private final ApricodeApprovalResponseMapper requestApprovalResponseMapper = new ApricodeApprovalResponseMapper();

    public void approvalRequest(User request) {
        AppricodeApprovalRequestRequest reqExternal = requestApprovalRequestMapper.map(request);
        String rawResponse = webClientBuilder.build()
            .post()
            .uri(properties.getBaseUrl() + "/webhook/approval-request")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(reqExternal)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        log.debug("Appricode approval request response: {}", rawResponse);
    }

    public void approvalResponse(User user, Long bookingId, List<BookingFlight> bookingFlights, List<BookingHotel> bookingHotels) {
        AppricodeApprovalResponseRequest reqExternal = requestApprovalResponseMapper.map(user, bookingId, bookingFlights, bookingHotels);
        String rawResponse = webClientBuilder.build()
            .post()
            .uri(properties.getBaseUrl() + "/webhook/approval-response")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(reqExternal)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        log.debug("Appricode approval response response: {}", rawResponse);
    }

}
