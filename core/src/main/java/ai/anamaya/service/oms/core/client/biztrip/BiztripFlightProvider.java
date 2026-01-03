package ai.anamaya.service.oms.core.client.biztrip;

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
    private final BiztripFlightBookingSearchDataService biztripFlightBookingSearchDataService;
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
    public BookingFlightSubmitResponse submitBooking(CallerContext callerContext, FlightBookingSubmitRequest request) {
        return bookingSubmitService.submit(callerContext, request);
    }

    @Override
    public BookingFlightSubmitResponse payment(CallerContext callerContext, FlightBookingPaymentRequest request) {
        return biztripFlightBookingPaymentService.payment(callerContext, request);
    }

    @Override
    public BookingFlightSubmitResponse checkStatus(CallerContext callerContext, FlightBookingStatusCheckRequest request) {
        return biztripFlightBookingCheckStatusService.checkStatus(callerContext, request);
    }

    @Override
    public List<BookingDataResponse> searchData(CallerContext callerContext, FlightBookingSearchDataRequest request) {
        return biztripFlightBookingSearchDataService.search(callerContext, request);
    }
}
