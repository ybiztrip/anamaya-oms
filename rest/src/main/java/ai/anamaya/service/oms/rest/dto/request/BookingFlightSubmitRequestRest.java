package ai.anamaya.service.oms.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookingFlightSubmitRequestRest {

    @NotNull
    @Valid
    private List<BookingFlightRequestRest> flights;

    @NotEmpty
    @Valid
    private List<BookingPaxRequestRest> paxs;

}
