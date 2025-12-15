package ai.anamaya.service.oms.core.dto.response;

import ai.anamaya.service.oms.core.enums.BookingStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long companyId;
    private String code;
    private String journeyCode;
    private LocalDate startDate;
    private LocalDate endDate;
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
    private BookingStatus status;

    private List<BookingFlightResponse> flights;
    private List<BookingHotelResponse> hotels;
}
