package ai.anamaya.service.oms.core.client.apricode.mapper;

import ai.anamaya.service.oms.core.client.apricode.dto.request.AppricodeApprovalResponseRequest;
import ai.anamaya.service.oms.core.entity.BookingFlight;
import ai.anamaya.service.oms.core.entity.BookingHotel;
import ai.anamaya.service.oms.core.entity.User;

import java.math.BigDecimal;
import java.util.List;

public class ApricodeApprovalResponseMapper {

    public AppricodeApprovalResponseRequest map(User user, Long bookingId, List<BookingFlight> bookingFlights, List<BookingHotel> bookingHotels) {
        AppricodeApprovalResponseRequest req = new AppricodeApprovalResponseRequest();

        AppricodeApprovalResponseRequest.Booking booking = new AppricodeApprovalResponseRequest.Booking();

        booking.setBookingId(bookingId);

        booking.setFlights(
            bookingFlights.stream()
                .map(f -> {
                    AppricodeApprovalResponseRequest.Flight flight =
                        new AppricodeApprovalResponseRequest.Flight();
                    flight.setId(f.getId());
                    flight.setOrigin(f.getOrigin());
                    flight.setTotalAmount(f.getTotalAmount());
                    flight.setDestination(f.getDestination());
                    flight.setDepartureDatetime(f.getDepartureDatetime());
                    flight.setArrivalDatetime(f.getArrivalDatetime());
                    flight.setStatus(f.getStatus());
                    return flight;
                })
                .toList()
        );

        // ---- Hotels ----
        booking.setHotels(
            bookingHotels.stream()
                .map(h -> {
                    AppricodeApprovalResponseRequest.Hotel hotel =
                        new AppricodeApprovalResponseRequest.Hotel();
                    hotel.setId(h.getId());
                    hotel.setTotalAmount(BigDecimal.valueOf(h.getPartnerSellAmount()));
                    hotel.setCheckInDate(h.getCheckInDate());
                    hotel.setCheckOutDate(h.getCheckOutDate());
                    hotel.setStatus(h.getStatus());
                    return hotel;
                })
                .toList()
        );

        req.setBooking(booking);

        AppricodeApprovalResponseRequest.User approver = new AppricodeApprovalResponseRequest.User();
        approver.setEmail(user.getEmail());
        approver.setFirstName(user.getFirstName());
        approver.setLastName(user.getLastName());
        approver.setPhoneNo(user.getPhoneNo());

        req.setUser(approver);
        return req;
    }

}
