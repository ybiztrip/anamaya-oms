package ai.anamaya.service.oms.core.client.biztrip.mapper.request;


import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request.BiztripHotelBookingCreateRequest;
import ai.anamaya.service.oms.core.dto.request.booking.hotel.HotelBookingCreateRequest;

import java.util.stream.Collectors;

public class BiztripHotelBookingSubmitRequestMapper {

    public BiztripHotelBookingCreateRequest map(HotelBookingCreateRequest source) {
        BiztripHotelBookingCreateRequest target =
            new BiztripHotelBookingCreateRequest();

        target.setPropertyId(source.getPropertyId());
        target.setPartnerBookingId(source.getPartnerBookingId());
        target.setCheckInDate(source.getCheckInDate());
        target.setCheckOutDate(source.getCheckOutDate());
        target.setLanguage(source.getLanguage());
        target.setUserNationality(source.getUserNationality());
        target.setSpecialRequest(source.getSpecialRequest());
        target.setDisplayCurrency(source.getDisplayCurrency());
        target.setAdditionalData(source.getAdditionalData());

        // Rooms
        target.setRooms(
            source.getRooms().stream()
                .map(room -> {
                    BiztripHotelBookingCreateRequest.Room r =
                        new BiztripHotelBookingCreateRequest.Room();

                    r.setRoomId(room.getRoomId());
                    r.setRateKey(room.getPaymentKey());
                    r.setNumRooms(room.getNumRooms());
                    r.setNumAdults(room.getNumAdults());
                    r.setNumChild(room.getNumChild());
                    r.setChildrenAges(room.getChildrenAges());

                    r.setGuestInfo(
                        room.getGuestInfo().stream()
                            .map(g -> {
                                BiztripHotelBookingCreateRequest.GuestInfo gi =
                                    new BiztripHotelBookingCreateRequest.GuestInfo();
                                gi.setFirstName(g.getFirstName());
                                gi.setLastName(g.getLastName());
                                gi.setGender(g.getGender());
                                gi.setTitle(g.getTitle());
                                gi.setEmail(g.getEmail());
                                gi.setIdtype(g.getIdtype());
                                gi.setIdnumber(g.getIdnumber());
                                return gi;
                            })
                            .collect(Collectors.toList())
                    );

                    return r;
                })
                .collect(Collectors.toList())
        );

        // Total Rates
        BiztripHotelBookingCreateRequest.TotalRates rates =
            new BiztripHotelBookingCreateRequest.TotalRates();
        rates.setPartnerSellAmount(source.getTotalRates().getPartnerSellAmount());
        rates.setPartnerNettAmount(source.getTotalRates().getPartnerNettAmount());
        target.setTotalRates(rates);

        // Customer
        BiztripHotelBookingCreateRequest.CustomerInfo customer =
            new BiztripHotelBookingCreateRequest.CustomerInfo();
        customer.setTitle(source.getCustomerInfo().getTitle());
        customer.setFirstName(source.getCustomerInfo().getFirstName());
        customer.setLastName(source.getCustomerInfo().getLastName());
        customer.setEmail(source.getCustomerInfo().getEmail());
        customer.setPhone(source.getCustomerInfo().getPhone());
        target.setCustomerInfo(customer);

        // Payment
        BiztripHotelBookingCreateRequest.UserPayment payment =
            new BiztripHotelBookingCreateRequest.UserPayment();
        payment.setUserPayment(source.getUserPayment().getUserPayment());
        target.setUserPayment(payment);

        return target;
    }
}
