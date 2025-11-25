package ai.anamaya.service.oms.core.client.biztrip.dto.submit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BiztripBookingPassengers {
    private List<BiztripBookingPassenger> adults;
    private List<BiztripBookingPassenger> children;
    private List<BiztripBookingPassenger> infants;
}
