package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

import java.util.List;

@Data
public class BiztripFlightBookingDetail {

    private BiztripContactDetail contactDetail;

    private BiztripPassengers passengers;

    private BiztripFareDetails fareDetail;

    private List<BiztripJourney> journeys;

    private BiztripPrice grandTotalFareWithCurrency;

    private Long bookingTime;

    private String status;

    private String issuanceFailedReason;

    private String local;
}
