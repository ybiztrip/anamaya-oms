package ai.anamaya.service.oms.core.client.chatEngine;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "external.chat-engine")
public class ChatEngineProperties {
    private String adminToken;
    private String baseUrl;
}