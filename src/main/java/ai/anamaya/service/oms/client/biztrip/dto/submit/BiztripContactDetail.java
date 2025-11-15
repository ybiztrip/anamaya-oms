package ai.anamaya.service.oms.client.biztrip.dto.submit;

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
