package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.BookingFlightStatus;
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
public class BookingFlightListFilter {

    private List<BookingFlightStatus> statuses;
    private Long companyId;
    private Long userId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
