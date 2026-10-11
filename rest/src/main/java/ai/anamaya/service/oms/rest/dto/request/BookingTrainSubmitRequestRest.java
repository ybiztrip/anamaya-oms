package ai.anamaya.service.oms.rest.dto.request;

import ai.anamaya.service.oms.core.dto.request.BookingTrainRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookingTrainSubmitRequestRest {

    @NotNull
    @Valid
    private BookingTrainRequest train;

    @NotEmpty
    @Valid
    private List<BookingPaxRequestRest> paxs;

}
