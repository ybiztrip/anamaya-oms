package ai.anamaya.service.oms.core.client.biztrip.dto.submit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiztripBookingSubmitRequest {
    private BiztripBookingContactDetail contactDetail;
    private BiztripBookingPassengers passengers;
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
