package ai.anamaya.service.oms.controller;

import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.FlightAirportResponse;
import ai.anamaya.service.oms.service.BiztripFlightAirportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flight/airports")
public class FlightAirportController {

    private final BiztripFlightAirportService biztripFlightAirportService;

    public FlightAirportController(BiztripFlightAirportService biztripFlightAirportService) {
        this.biztripFlightAirportService = biztripFlightAirportService;
    }

    @GetMapping
    public ApiResponse<List<FlightAirportResponse>> getAllAirports() {
        return biztripFlightAirportService.getAirports();
    }
}
