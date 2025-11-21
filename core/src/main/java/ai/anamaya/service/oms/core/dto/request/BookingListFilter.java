package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingListFilter {

    private List<BookingStatus> statuses;
    private Long companyId;
    private Long userId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
