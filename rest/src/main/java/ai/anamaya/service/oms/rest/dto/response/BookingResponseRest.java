package ai.anamaya.service.oms.rest.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class BookingResponseRest {
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
    private String status;

    private List<BookingPaxResponseRest> paxList;
    private List<BookingFlightResponseRest> flightList;
    private List<BookingHotelResponseRest> hotelList;
}
