package ai.anamaya.service.oms.core.client.chatEngine.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatEngineUserRegisterRequest {

    private String phoneNumber;
    private String countryCode;
    private String email;
    private String firstName;
    private String lastName;
    private UserData userData;

    @Data
    public static class UserData {
        private String gender;
        private String nationality;
        private String title;
    }
}