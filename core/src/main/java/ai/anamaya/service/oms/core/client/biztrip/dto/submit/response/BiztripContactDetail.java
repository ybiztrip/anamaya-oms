package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

@Data
public class BiztripContactDetail {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String phoneNumberCountryCode;
    private String customerEmail;
    private String customerPhoneNumber;
    private String customerPhoneNumberCountryCode;
}
