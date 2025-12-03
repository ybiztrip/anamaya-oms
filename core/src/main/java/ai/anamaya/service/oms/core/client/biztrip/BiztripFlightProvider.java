package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.FlightAddOnsRequest;
import ai.anamaya.service.oms.core.dto.request.FlightOneWaySearchRequest;
import ai.anamaya.service.oms.core.dto.request.booking.payment.BookingPaymentRequest;
import ai.anamaya.service.oms.core.dto.request.booking.status.BookingStatusCheckRequest;
import ai.anamaya.service.oms.core.dto.request.booking.submit.BookingSubmitRequest;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.dto.response.booking.submit.BookingSubmitResponse;
import ai.anamaya.service.oms.core.service.FlightProvider;
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
    private final BiztripFlightBookingCheckStatusService  biztripFlightBookingCheckStatusService;
    private final BiztripFlightSearchService searchService;
    private final BiztripFlightBookingSubmitService bookingSubmitService;
    private final BiztripFlightBookingPaymentService biztripFlightBookingPaymentService;

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

    @Override
    public BookingSubmitResponse submitBooking(BookingSubmitRequest request) {
        return bookingSubmitService.submit(request);
    }

    @Override
    public BookingSubmitResponse payment(CallerContext callerContext, BookingPaymentRequest request) {
        return biztripFlightBookingPaymentService.payment(callerContext, request);
    }

    @Override
    public BookingSubmitResponse checkStatus(CallerContext callerContext, BookingStatusCheckRequest request) {
        return biztripFlightBookingCheckStatusService.checkStatus(callerContext, request);
    }
}
