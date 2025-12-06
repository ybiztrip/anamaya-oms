package ai.anamaya.service.oms.core.client.biztrip.mapper.request;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.request.*;
import ai.anamaya.service.oms.core.dto.request.booking.submit.FlightBookingSubmitRequest;
import ai.anamaya.service.oms.core.dto.request.booking.submit.Passenger;
import ai.anamaya.service.oms.core.dto.request.booking.submit.Passengers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BiztripFlightBookingSubmitRequestMapper {

    public BiztripBookingSubmitRequest map(FlightBookingSubmitRequest request) {

        BiztripBookingSubmitRequest dto = new BiztripBookingSubmitRequest();

        dto.setFlightIds(request.getFlightIds());
        dto.setPartnerBookingId(request.getPartnerBookingId());
        dto.setDestinationId(request.getDestinationId());
        dto.setJourneyType(request.getJourneyType());
        dto.setLocale(request.getLocale());
        dto.setLoginID(request.getContactDetail().getCustomerEmail());
        dto.setLoginType(request.getLoginType());
        dto.setCustomerLoginID(request.getContactDetail().getCustomerEmail());
        dto.setCustomerLoginType(request.getCustomerLoginType());
        dto.setSource(request.getSource());
        dto.setJabatan(request.getJabatan());
        dto.setAdditionalData(convertAdditionalData(request.getAdditionalData()));

        // CONTACT DETAIL
        dto.setContactDetail(BiztripBookingContactDetail.builder()
            .email(request.getContactDetail().getEmail())
            .customerEmail(request.getContactDetail().getEmail())
            .firstName(request.getContactDetail().getFirstName())
            .lastName(request.getContactDetail().getLastName())
            .phoneNumber(request.getContactDetail().getPhoneNumber())
            .phoneNumberCountryCode(request.getContactDetail().getPhoneNumberCountryCode())
            .customerPhoneNumber(request.getContactDetail().getPhoneNumber())
            .customerPhoneNumberCountryCode(request.getContactDetail().getPhoneNumberCountryCode())
            .title(request.getContactDetail().getTitle())
            .dateOfBirth(convertDateFormat(request.getContactDetail().getDateOfBirth()))
            .build()
        );

        // PASSENGERS
        Passengers reqPassengers = request.getPassengers();

        dto.setPassengers(new BiztripBookingPassengers(
            mapPassengers(reqPassengers.getAdults()),
            mapPassengers(reqPassengers.getChildren()),
            mapPassengers(reqPassengers.getInfants())
        ));

        return dto;
    }

    private List<BiztripBookingPassenger> mapPassengers(List<Passenger> passengers) {
        if (passengers == null) return List.of();

        return passengers.stream()
            .map(this::mapPassenger)
            .toList();
    }

    private BiztripBookingPassenger mapPassenger(Passenger p) {
        return BiztripBookingPassenger.builder()
            .title(p.getTitle())
            .firstName(p.getFirstName())
            .lastName(p.getLastName())
            .gender(convertGender(p.getGender()))
            .dateOfBirth(convertDateFormat(p.getDateOfBirth()))
            .nationality(p.getNationality())
            .documentDetail(
                BiztripBookingDocumentDetail.builder()
                    .documentNo(p.getDocumentDetail().getDocumentNo())
                    .documentType(p.getDocumentDetail().getDocumentType())
                    .issuingCountry(p.getDocumentDetail().getIssuingCountry())
                    .expirationDate(convertDateFormat(p.getDocumentDetail().getExpirationDate()))
                    .build()
            )
            .addOns(p.getAddOns())
            .build();
    }

    private String convertAdditionalData(String additionalData) {
        if (additionalData.isEmpty() || additionalData.equalsIgnoreCase("{}")) return "";
        return additionalData;
    }

    private String convertGender(String gender) {
        if (gender == null) return null;
        return switch (gender.toUpperCase()) {
            case "MALE" -> "M";
            case "FEMALE" -> "F";
            default -> gender;
        };
    }

    private String convertDateFormat(String date) {
        if (date == null || date.isBlank()) return "";

        try {
            LocalDate localDate = LocalDate.parse(date);
            return localDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        } catch (Exception e) {
            return date;
        }
    }

}
