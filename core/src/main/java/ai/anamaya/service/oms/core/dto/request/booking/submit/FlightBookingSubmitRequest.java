package ai.anamaya.service.oms.core.dto.request.booking.submit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightBookingSubmitRequest {
    private ContactDetail contactDetail;
    private Passengers passengers;
    private List<String> flightIds;
    private String partnerBookingId;
    private String destinationId;
    private String journeyType;
    private String locale;
    private String loginID;
    private String loginType;
    private String customerLoginID;
    private String customerLoginType;
    private String source;
    private String jabatan;
    private String additionalData;
}
