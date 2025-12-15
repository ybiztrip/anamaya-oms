package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.*;
import ai.anamaya.service.oms.core.dto.response.BookingFlightResponse;
import ai.anamaya.service.oms.core.dto.response.BookingHotelResponse;
import ai.anamaya.service.oms.core.dto.response.BookingPaxResponse;
import ai.anamaya.service.oms.core.dto.response.BookingResponse;
import ai.anamaya.service.oms.rest.dto.request.*;
import ai.anamaya.service.oms.rest.dto.response.BookingFlightResponseRest;
import ai.anamaya.service.oms.rest.dto.response.BookingHotelResponseRest;
import ai.anamaya.service.oms.rest.dto.response.BookingPaxResponseRest;
import ai.anamaya.service.oms.rest.dto.response.BookingResponseRest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingRequest toCore(BookingRequestRest rest);
    BookingApproveRequest toCoreApprove(BookingApproveRequestRest rest);

    BookingPaxRequest toCore(BookingPaxRequestRest rest);
    List<BookingPaxRequest> toCorePax(List<BookingPaxRequestRest> list);

    BookingFlightRequest toCore(BookingFlightRequestRest rest);
    BookingFlightSubmitRequest toCoreSubmitFlights(BookingFlightSubmitRequestRest rest);

    BookingHotelRequest toCore(BookingHotelRequestRest rest);
    BookingHotelSubmitRequest toCoreSubmitHotel(BookingHotelSubmitRequestRest rest);

    BookingResponseRest toRest(BookingResponse core);

    BookingPaxResponseRest toRest(BookingPaxResponse core);
    List<BookingPaxResponseRest> toRestPaxs(List<BookingPaxResponse> list);

    BookingFlightResponseRest toRest(BookingFlightResponse core);
    List<BookingFlightResponseRest> toRestFlights(List<BookingFlightResponse> list);

    BookingHotelResponseRest toRest(BookingHotelResponse core);
    List<BookingHotelResponseRest> toRestHotels(List<BookingHotelResponse> list);
}

