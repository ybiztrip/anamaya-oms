package ai.anamaya.service.oms.dto.request.booking.submit;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BookingSubmitRequest {
    private ContactDetail contactDetail;
    private Passengers passengers;
    private List<String> flightIds;
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
