package ai.anamaya.service.oms.core.service;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.FlightAddOnsRequest;
import ai.anamaya.service.oms.core.dto.request.FlightOneWaySearchRequest;
import ai.anamaya.service.oms.core.dto.request.booking.payment.BookingPaymentRequest;
import ai.anamaya.service.oms.core.dto.request.booking.status.BookingStatusCheckRequest;
import ai.anamaya.service.oms.core.dto.request.booking.submit.BookingSearchDataRequest;
import ai.anamaya.service.oms.core.dto.request.booking.submit.BookingSubmitRequest;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.dto.response.booking.data.BookingDataResponse;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingSubmitResponse;

import java.util.List;

public interface FlightProvider {
    ApiResponse<List<FlightAirportResponse>> getAirports();
    ApiResponse<List<FlightAirlineResponse>> getAirlines();
    ApiResponse<FlightBookingRuleResponse> getBookingRules(String airlineCode);
    ApiResponse<FlightAddOnsResponse> getAddOns(FlightAddOnsRequest request);
    ApiResponse<FlightOneWaySearchResponse> searchOneWay(FlightOneWaySearchRequest request);

    BookingSubmitResponse submitBooking(BookingSubmitRequest request);
    BookingSubmitResponse payment(CallerContext callerContext, BookingPaymentRequest request);
    BookingSubmitResponse checkStatus(CallerContext callerContext, BookingStatusCheckRequest request);
    List<BookingDataResponse> searchData(CallerContext callerContext, BookingSearchDataRequest request);
}
