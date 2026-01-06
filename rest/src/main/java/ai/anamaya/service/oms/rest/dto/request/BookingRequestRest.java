package ai.anamaya.service.oms.rest.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class BookingRequestRest {
    private String journeyCode;

    @NotNull(message = "startDate is required")
    private LocalDate startDate;

    @NotNull(message = "endDate is required")
    private LocalDate endDate;

    @AssertTrue(message = "startDate must be less than endDate")
    public boolean isJourneyDateValid() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !startDate.isAfter(endDate);
    }

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
