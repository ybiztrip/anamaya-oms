package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.dto.request.TrainAvailabilityRequest;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.TrainAvailabilityResponse;
import ai.anamaya.service.oms.core.dto.response.TrainStationResponse;
import ai.anamaya.service.oms.core.service.TrainService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/train")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    @GetMapping("/all-station")
    public ApiResponse<List<TrainStationResponse>> getAllStations() {
        return trainService.getAllStations();
    }

    @PostMapping("/get-availability")
    public ApiResponse<TrainAvailabilityResponse> getAvailability(
            @Valid @RequestBody TrainAvailabilityRequest request
    ) {
        return trainService.getAvailability(request);
    }
}
