package ai.anamaya.service.oms.core.client.biztrip.dto.submit.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BiztripBookingCheckFullStatusRequest {
    private List<String> bookingIds;
}
