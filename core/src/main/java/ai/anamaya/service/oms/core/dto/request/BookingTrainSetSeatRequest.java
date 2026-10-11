package ai.anamaya.service.oms.core.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookingTrainSetSeatRequest {
    @NotNull
    private Long trainId;

    // empty = keep the seats Biztrip auto-assigned on book
    @Valid
    private List<Seat> seats;

    @Data
    public static class Seat {
        @NotNull
        private Long paxId;

        @NotBlank
        private String coachName;

        @NotBlank
        private String seatNumber;
    }
}
