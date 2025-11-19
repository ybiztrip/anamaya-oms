package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.BookingFlightRequest;
import ai.anamaya.service.oms.core.dto.request.BookingHotelRequest;
import ai.anamaya.service.oms.core.dto.request.BookingPaxRequest;
import ai.anamaya.service.oms.core.dto.request.BookingRequest;
import ai.anamaya.service.oms.core.dto.response.BookingFlightResponse;
import ai.anamaya.service.oms.core.dto.response.BookingHotelResponse;
import ai.anamaya.service.oms.core.dto.response.BookingPaxResponse;
import ai.anamaya.service.oms.core.dto.response.BookingResponse;
import ai.anamaya.service.oms.rest.dto.request.BookingFlightRequestRest;
import ai.anamaya.service.oms.rest.dto.request.BookingHotelRequestRest;
import ai.anamaya.service.oms.rest.dto.request.BookingPaxRequestRest;
import ai.anamaya.service.oms.rest.dto.request.BookingRequestRest;
import ai.anamaya.service.oms.rest.dto.response.BookingFlightResponseRest;
import ai.anamaya.service.oms.rest.dto.response.BookingHotelResponseRest;
import ai.anamaya.service.oms.rest.dto.response.BookingPaxResponseRest;
import ai.anamaya.service.oms.rest.dto.response.BookingResponseRest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingRequest toCore(BookingRequestRest rest);

    BookingPaxRequest toCore(BookingPaxRequestRest rest);
    List<BookingPaxRequest> toCorePax(List<BookingPaxRequestRest> list);

    BookingFlightRequest toCore(BookingFlightRequestRest rest);
    List<BookingFlightRequest> toCoreFlights(List<BookingFlightRequestRest> list);

    BookingHotelRequest toCore(BookingHotelRequestRest rest);
    List<BookingHotelRequest> toCoreHotels(List<BookingHotelRequestRest> list);

    BookingResponseRest toRest(BookingResponse core);

    BookingPaxResponseRest toRest(BookingPaxResponse core);
    BookingFlightResponseRest toRest(BookingFlightResponse core);
    BookingHotelResponseRest toRest(BookingHotelResponse core);
}
