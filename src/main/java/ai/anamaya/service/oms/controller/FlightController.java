package ai.anamaya.service.oms.controller;

import ai.anamaya.service.oms.dto.request.FlightOneWaySearchRequest;
import ai.anamaya.service.oms.dto.response.*;
import ai.anamaya.service.oms.service.BiztripFlightAirlineService;
import ai.anamaya.service.oms.service.BiztripFlightAirportService;
import ai.anamaya.service.oms.service.BiztripFlightBookingRuleService;
import ai.anamaya.service.oms.service.BiztripFlightSearchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flight")
public class FlightController {

    private final BiztripFlightAirportService airportService;
    private final BiztripFlightAirlineService airlineService;
    private final BiztripFlightBookingRuleService bookingRuleService;
    private final BiztripFlightSearchService searchService;

    public FlightController(
            BiztripFlightAirportService airportService,
            BiztripFlightAirlineService airlineService,
            BiztripFlightBookingRuleService bookingRuleService,
            BiztripFlightSearchService searchService) {
        this.airportService = airportService;
        this.airlineService = airlineService;
        this.bookingRuleService = bookingRuleService;
        this.searchService = searchService;
    }

    @GetMapping("/airports")
    public ApiResponse<List<BiztripFlightAirportResponse>> getAllAirports() {
        return airportService.getAirports();
    }

    @GetMapping("/airlines")
    public ApiResponse<List<BiztripFlightAirlineResponse>> getAllAirlines() {
        return airlineService.getAirlines();
    }

    @GetMapping("/booking/rules")
    public ApiResponse<BiztripFlightBookingRuleResponse> getBookingRules(
            @RequestParam String airlineCode
    ) {
        return bookingRuleService.getBookingRules(airlineCode);
    }

    @PostMapping("/search/one-way")
    public ApiResponse<BiztripFlightOneWaySearchResponse> searchOneWay(
            @Valid @RequestBody FlightOneWaySearchRequest request
    ) {
        return searchService.searchOneWay(request);
    }

}
