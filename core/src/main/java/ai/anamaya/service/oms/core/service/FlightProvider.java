package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.FlightAddOnsRequest;
import ai.anamaya.service.oms.core.dto.request.FlightOneWaySearchRequest;
import ai.anamaya.service.oms.core.dto.request.booking.payment.FlightBookingPaymentRequest;
import ai.anamaya.service.oms.core.dto.request.booking.status.FlightBookingStatusCheckRequest;
import ai.anamaya.service.oms.core.dto.request.booking.submit.FlightBookingSearchDataRequest;
import ai.anamaya.service.oms.core.dto.request.booking.submit.FlightBookingSubmitRequest;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.dto.response.booking.data.BookingDataResponse;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingFlightSubmitResponse;

import java.util.List;

public interface FlightProvider {
    ApiResponse<List<FlightAirportResponse>> getAirports();
    ApiResponse<List<FlightAirlineResponse>> getAirlines();
    ApiResponse<FlightBookingRuleResponse> getBookingRules(String airlineCode);
    FlightAddOnsResponse getAddOns(CallerContext callerContext, FlightAddOnsRequest request);
    ApiResponse<FlightOneWaySearchResponse> searchOneWay(FlightOneWaySearchRequest request);

    BookingFlightSubmitResponse submitBooking(CallerContext callerContext, FlightBookingSubmitRequest request);
    BookingFlightSubmitResponse payment(CallerContext callerContext, FlightBookingPaymentRequest request);
    BookingFlightSubmitResponse checkStatus(CallerContext callerContext, FlightBookingStatusCheckRequest request);
    List<BookingDataResponse> searchData(CallerContext callerContext, FlightBookingSearchDataRequest request);
}
