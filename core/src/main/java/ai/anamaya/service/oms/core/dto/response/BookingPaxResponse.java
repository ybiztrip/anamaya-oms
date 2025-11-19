package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.PaxGender;
import ai.anamaya.service.oms.core.enums.PaxTitle;
import ai.anamaya.service.oms.core.enums.PaxType;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingPaxResponse {
    private Long id;
    private Long bookingId;
    private String email;
    private String firstName;
    private String lastName;
    private PaxGender gender;
    private PaxType type;
    private PaxTitle title;
    private String nationality;
    private String phoneCode;
    private String phoneNumber;
    private LocalDate dob;
    private List<Map<String, Object>> addOn;
    private String issuingCountry;
    private String documentType;
    private String documentNo;
    private LocalDate expirationDate;
}
