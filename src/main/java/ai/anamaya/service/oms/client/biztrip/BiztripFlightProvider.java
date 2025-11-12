package ai.anamaya.service.oms.client.biztrip;

import ai.anamaya.service.oms.dto.request.FlightAddOnsRequest;
import ai.anamaya.service.oms.dto.request.FlightOneWaySearchRequest;
import ai.anamaya.service.oms.dto.response.*;
import ai.anamaya.service.oms.service.FlightProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("biztripFlightProvider")
@RequiredArgsConstructor
public class BiztripFlightProvider implements FlightProvider {

    private final BiztripFlightAirportService airportService;
    private final BiztripFlightAirlineService airlineService;
    private final BiztripFlightBookingRuleService bookingRuleService;
    private final BiztripFlightBookingAddOnsService addOnsService;
    private final BiztripFlightSearchService searchService;

    @Override
    public ApiResponse<List<FlightAirportResponse>> getAirports() {
        return airportService.getAirports();
    }

    @Override
    public ApiResponse<List<FlightAirlineResponse>> getAirlines() {
        return airlineService.getAirlines();
    }

    @Override
    public ApiResponse<FlightBookingRuleResponse> getBookingRules(String airlineCode) {
        return bookingRuleService.getBookingRules(airlineCode);
    }

    @Override
    public ApiResponse<FlightAddOnsResponse> getAddOns(FlightAddOnsRequest request) {
        return addOnsService.getAddOns(request);
    }

    @Override
    public ApiResponse<FlightOneWaySearchResponse> searchOneWay(FlightOneWaySearchRequest request) {
        return searchService.searchOneWay(request);
    }
}
