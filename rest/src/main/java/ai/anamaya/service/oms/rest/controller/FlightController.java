package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.dto.request.FlightAddOnsRequest;
import ai.anamaya.service.oms.core.dto.request.FlightOneWaySearchRequest;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.service.FlightService;
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
    public ApiResponse<List<FlightAirportResponse>> getAllAirports(
            @RequestParam(defaultValue = "biztrip") String source
    ) {
        return flightService.getAirports(source);
    }

    @GetMapping("/airlines")
    public ApiResponse<List<FlightAirlineResponse>> getAllAirlines(
            @RequestParam(defaultValue = "biztrip") String source
    ) {
        return flightService.getAirlines(source);
    }

    @GetMapping("/booking/rules")
    public ApiResponse<FlightBookingRuleResponse> getBookingRules(
            @RequestParam(defaultValue = "biztrip") String source,
            @RequestParam String airlineCode
    ) {
        return flightService.getBookingRules(source, airlineCode);
    }

    @PostMapping("/booking/add-ons")
    public ApiResponse<FlightAddOnsResponse> getAddOns(
            @RequestParam(defaultValue = "biztrip") String source,
            @RequestBody FlightAddOnsRequest request
    ) {
        return flightService.getAddOns(source, request);
    }

    @PostMapping("/search/one-way")
    public ApiResponse<FlightOneWaySearchResponse> searchOneWay(
            @RequestParam(defaultValue = "biztrip") String source,
            @Valid @RequestBody FlightOneWaySearchRequest request
    ) {
        return flightService.searchOneWay(source, request);
    }

}
