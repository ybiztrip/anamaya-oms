package ai.anamaya.service.oms.core.client.biztrip.dto.submit.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BiztripBookingContactDetail {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String phoneNumberCountryCode;
    private String customerEmail;
    private String customerPhoneNumber;
    private String customerPhoneNumberCountryCode;
    private String title;
    private String dateOfBirth;
}
