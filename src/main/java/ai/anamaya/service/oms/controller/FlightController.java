package ai.anamaya.service.oms.controller;

import ai.anamaya.service.oms.dto.request.FlightOneWaySearchRequest;
import ai.anamaya.service.oms.dto.response.*;
import ai.anamaya.service.oms.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flight")
public class FlightController {

    private final FlightService flightService;

    public FlightController(
            FlightService flightService
            ) {
        this.flightService = flightService;
    }

    @GetMapping("/airports")
    public ApiResponse<List<FlightAirportResponse>> getAllAirports() {
        return flightService.getAirports();
    }

    @GetMapping("/airlines")
    public ApiResponse<List<FlightAirlineResponse>> getAllAirlines() {
        return flightService.getAirlines();
    }

    @GetMapping("/booking/rules")
    public ApiResponse<FlightBookingRuleResponse> getBookingRules(
            @RequestParam String airlineCode
    ) {
        return flightService.getBookingRules(airlineCode);
    }

    @PostMapping("/search/one-way")
    public ApiResponse<FlightOneWaySearchResponse> searchOneWay(
            @Valid @RequestBody FlightOneWaySearchRequest request
    ) {
        return flightService.searchOneWay(request);
    }

}
