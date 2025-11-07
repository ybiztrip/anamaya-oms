package ai.anamaya.service.oms.service;

import ai.anamaya.service.oms.dto.request.FlightOneWaySearchRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.FlightAirlineResponse;
import ai.anamaya.service.oms.dto.response.FlightAirportResponse;
import ai.anamaya.service.oms.dto.response.FlightBookingRuleResponse;
import ai.anamaya.service.oms.dto.response.FlightOneWaySearchResponse;

import java.util.List;

public interface FlightProvider {
    ApiResponse<List<FlightAirportResponse>> getAirports();
    ApiResponse<List<FlightAirlineResponse>> getAirlines();
    ApiResponse<FlightBookingRuleResponse> getBookingRules(String airlineCode);
    ApiResponse<FlightOneWaySearchResponse> searchOneWay(FlightOneWaySearchRequest request);
}
