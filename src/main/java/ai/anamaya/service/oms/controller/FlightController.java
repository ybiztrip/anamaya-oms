package ai.anamaya.service.oms.controller;

import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.BiztripFlightAirlineResponse;
import ai.anamaya.service.oms.dto.response.BiztripFlightAirportResponse;
import ai.anamaya.service.oms.service.BiztripFlightAirlineService;
import ai.anamaya.service.oms.service.BiztripFlightAirportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flight")
public class FlightController {

    private final BiztripFlightAirportService airportService;
    private final BiztripFlightAirlineService airlineService;

    public FlightController(BiztripFlightAirportService airportService,
                                   BiztripFlightAirlineService airlineService) {
        this.airportService = airportService;
        this.airlineService = airlineService;
    }

    @GetMapping("/airports")
    public ApiResponse<List<BiztripFlightAirportResponse>> getAllAirports() {
        return airportService.getAirports();
    }

    @GetMapping("/airlines")
    public ApiResponse<List<BiztripFlightAirlineResponse>> getAllAirlines() {
        return airlineService.getAirlines();
    }
}
