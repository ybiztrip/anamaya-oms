package ai.anamaya.service.oms.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiztripTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expired_in")
    private long expiredIn;

    @JsonProperty("token_type")
    private String tokenType;
}
