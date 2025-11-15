package ai.anamaya.service.oms.dto.request.booking.submit;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Passengers {
    private List<Passenger> adults;
    private List<Passenger> children;
    private List<Passenger> infants;
}
