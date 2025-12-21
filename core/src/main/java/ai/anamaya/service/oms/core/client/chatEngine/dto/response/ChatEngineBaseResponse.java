package ai.anamaya.service.oms.core.client.chatEngine.dto.response;

import lombok.Data;

@Data
public class ChatEngineBaseResponse {
    private boolean success;
    private String message;
}
