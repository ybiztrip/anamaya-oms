package ai.anamaya.service.oms.dto.request;

import ai.anamaya.service.oms.enums.PaxTitle;
import ai.anamaya.service.oms.enums.PaxType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingPaxRequest {
    private Long id;
    private boolean isDeleted;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String email;
    private PaxType type;

    private PaxTitle title;

    private String nationality;
    private String phoneCode;
    private String phoneNumber;
    private LocalDate dob;
    private Map<String, Object> addOn;
    private String issuingCountry;
    private String documentType;
    private String documentNo;
    private LocalDate expirationDate;
}
