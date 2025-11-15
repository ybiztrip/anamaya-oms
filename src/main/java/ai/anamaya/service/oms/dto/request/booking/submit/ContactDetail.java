package ai.anamaya.service.oms.dto.request.booking.submit;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactDetail {
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
