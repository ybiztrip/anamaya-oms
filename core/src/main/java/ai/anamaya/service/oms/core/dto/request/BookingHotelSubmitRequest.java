package ai.anamaya.service.oms.core.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookingHotelSubmitRequest {
    @NotNull
    @Valid
    private BookingHotelRequest hotel;

    @NotEmpty
    @Valid
    private List<BookingPaxRequest> paxs;
}
