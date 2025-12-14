package ai.anamaya.service.oms.core.dto.request;

import lombok.*;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String journeyCode;
    private String contactEmail;
    private String contactFirstName;
    private String contactLastName;
    private String contactTitle;
    private String contactNationality;
    private String contactPhoneCode;
    private String contactPhoneNumber;
    private LocalDate contactDob;
    private Map<String, Object> additionalInfo;
    private Map<String, Object> clientAdditionalInfo;
}
