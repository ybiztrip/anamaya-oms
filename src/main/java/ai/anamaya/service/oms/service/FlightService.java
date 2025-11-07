package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.FlightOneWaySearchRequest;
import ai.anamaya.service.oms.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    @Qualifier("biztripFlightProvider")
    private final FlightProvider flightProvider;

    public ApiResponse<List<FlightAirportResponse>> getAirports() {
        return flightProvider.getAirports();
    }

    public ApiResponse<List<FlightAirlineResponse>> getAirlines() {
        return flightProvider.getAirlines();
    }

    public ApiResponse<FlightBookingRuleResponse> getBookingRules(String airline) {
        return flightProvider.getBookingRules(airline);
    }

    public ApiResponse<FlightOneWaySearchResponse> searchOneWay(FlightOneWaySearchRequest request) {
        return flightProvider.searchOneWay(request);
    }
}
