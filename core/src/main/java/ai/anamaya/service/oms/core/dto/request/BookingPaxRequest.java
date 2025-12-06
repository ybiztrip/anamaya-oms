package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.PaxGender;
import ai.anamaya.service.oms.core.enums.PaxTitle;
import ai.anamaya.service.oms.core.enums.PaxType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingPaxRequest {
    private Long id;
    private boolean deleted;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String email;

    @NotBlank
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
