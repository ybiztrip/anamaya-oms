package ai.anamaya.service.oms.rest.dto.request;

import lombok.Data;

import java.util.Map;

@Data
public class BookingRequestRest {
    private String journeyCode;

    private String contactEmail;
    private String contactFirstName;
    private String contactLastName;
    private String contactTitle;
    private String contactNationality;
    private String contactPhoneCode;
    private String contactPhoneNumber;
    private String contactDob;

    private Map<String, Object> additionalInfo;
    private Map<String, Object> clientAdditionalInfo;
}
