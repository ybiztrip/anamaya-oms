package ai.anamaya.service.oms.core.dto.request.booking.submit;

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
public class BookingSearchDataRequest {
    private Integer page;
    private Integer count;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> referenceCodes;
}
