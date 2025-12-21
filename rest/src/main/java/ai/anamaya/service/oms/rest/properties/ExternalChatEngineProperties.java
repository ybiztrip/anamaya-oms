package ai.anamaya.service.oms.rest.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "external.chat-engine")
@Getter
@Setter
public class ExternalChatEngineProperties {
    private String adminToken;
    private String agentToken;
}

