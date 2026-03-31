package ai.anamaya.service.oms.core.helper.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonHelper {

    private final ObjectMapper objectMapper;

    public JsonNode emptyObject() {
        return objectMapper.createObjectNode();
    }

    public String toJson(JsonNode node) {
        if (node == null) return null;
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON", e);
        }
    }
    public JsonNode toJsonNode(String json) {
        if (json == null) return null;
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON string", e);
        }
    }
}
