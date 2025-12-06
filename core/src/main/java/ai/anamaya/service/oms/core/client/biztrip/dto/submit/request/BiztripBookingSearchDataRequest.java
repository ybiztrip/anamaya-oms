package ai.anamaya.service.oms.core.client.biztrip.dto.submit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiztripBookingSearchDataRequest {
    private Integer page;
    private Integer count;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> bookingIds;
    private List<String> clients;
}
