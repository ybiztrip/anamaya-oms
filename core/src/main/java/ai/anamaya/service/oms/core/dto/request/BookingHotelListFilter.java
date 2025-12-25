package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.BookingHotelStatus;
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
public class BookingHotelListFilter {

    private List<BookingHotelStatus> statuses;
    private Long companyId;
    private Long userId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
