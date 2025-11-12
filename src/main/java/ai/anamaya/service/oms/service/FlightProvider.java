package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.FlightAddOnsRequest;
import ai.anamaya.service.oms.dto.request.FlightOneWaySearchRequest;
import ai.anamaya.service.oms.dto.response.*;

import java.util.List;

public interface FlightProvider {
    ApiResponse<List<FlightAirportResponse>> getAirports();
    ApiResponse<List<FlightAirlineResponse>> getAirlines();
    ApiResponse<FlightBookingRuleResponse> getBookingRules(String airlineCode);
    ApiResponse<FlightAddOnsResponse> getAddOns(FlightAddOnsRequest request);
    ApiResponse<FlightOneWaySearchResponse> searchOneWay(FlightOneWaySearchRequest request);
}
