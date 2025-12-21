package ai.anamaya.service.oms.core.client.chatEngine;

import ai.anamaya.service.oms.core.client.chatEngine.dto.request.ChatEngineUserRegisterRequest;
import ai.anamaya.service.oms.core.client.chatEngine.mapper.ChatEngineUserRegisterMapper;
import ai.anamaya.service.oms.core.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatEngineService {

    private final WebClient.Builder webClientBuilder;
    private final ChatEngineProperties properties;
    private final ChatEngineUserRegisterMapper requestMapper = new ChatEngineUserRegisterMapper();

    public void registerUser(User request) {
        ChatEngineUserRegisterRequest reqExternal = requestMapper.map(request);
        String rawResponse = webClientBuilder.build()
            .post()
            .uri(properties.getBaseUrl() + "/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", properties.getAdminToken())
            .bodyValue(reqExternal)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        log.debug("Chat engine raw response: {}", rawResponse);

        return;
    }

}