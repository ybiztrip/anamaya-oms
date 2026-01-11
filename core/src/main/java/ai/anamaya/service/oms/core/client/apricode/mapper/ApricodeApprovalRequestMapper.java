package ai.anamaya.service.oms.core.client.apricode.mapper;

import ai.anamaya.service.oms.core.client.apricode.dto.request.AppricodeApprovalRequestRequest;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ApricodeApprovalRequestMapper {

    public AppricodeApprovalRequestRequest map(User reqUserRequester, List<User> reqUserApprovers, Long bookingId, List<BookingFlight> bookingFlights, List<BookingHotel> bookingHotels) {
        AppricodeApprovalRequestRequest req = new AppricodeApprovalRequestRequest();

        AppricodeApprovalRequestRequest.Booking booking = new AppricodeApprovalRequestRequest.Booking();

        booking.setBookingId(bookingId);
        booking.setFlights(
            Optional.ofNullable(bookingFlights)
                .orElse(List.of())
                .stream()
                .map(f -> {
                    AppricodeApprovalRequestRequest.Flight flight =
                        new AppricodeApprovalRequestRequest.Flight();
                    flight.setId(f.getId());
                    flight.setOrigin(f.getOrigin());
                    flight.setTotalAmount(f.getTotalAmount());
                    flight.setDestination(f.getDestination());
                    flight.setDepartureDatetime(f.getDepartureDatetime());
                    flight.setArrivalDatetime(f.getArrivalDatetime());
                    return flight;
                })
                .toList()
        );

        // ---- Hotels ----
        booking.setHotels(
            bookingHotels
                .stream()
                .map(h -> {
                    AppricodeApprovalRequestRequest.Hotel hotel =
                        new AppricodeApprovalRequestRequest.Hotel();
                    hotel.setId(h.getId());
                    hotel.setTotalAmount(BigDecimal.valueOf(h.getPartnerSellAmount()));
                    hotel.setCheckInDate(h.getCheckInDate());
                    hotel.setCheckOutDate(h.getCheckOutDate());
                    return hotel;
                })
                .toList()
        );

        req.setBooking(booking);

        AppricodeApprovalRequestRequest.User userRequester = new AppricodeApprovalRequestRequest.User();
        userRequester.setEmail(reqUserRequester.getEmail());
        userRequester.setFirstName(reqUserRequester.getFirstName());
        userRequester.setLastName(reqUserRequester.getLastName());
        userRequester.setPhoneNo(reqUserRequester.getPhoneNo());
        req.setUserRequester(userRequester);

        req.setUsersApprovers(
            reqUserApprovers.stream()
                .map(data -> {
                    AppricodeApprovalRequestRequest.User userApprover = new AppricodeApprovalRequestRequest.User();
                    userApprover.setEmail(data.getEmail());
                    userApprover.setFirstName(data.getFirstName());
                    userApprover.setLastName(data.getLastName());
                    userApprover.setPhoneNo(data.getPhoneNo());
                    return userApprover;
                })
                .toList()
        );

        return req;
    }
}
