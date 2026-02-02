package ai.anamaya.service.oms.core.client.biztrip.mapper.response;

import ai.anamaya.service.oms.core.client.biztrip.dto.flight.response.*;
import ai.anamaya.service.oms.core.dto.response.*;

import java.util.List;


public class BiztripFlightAddOnsResponseMapper {

    public FlightAddOnsResponse map(Boolean isSuccess, BiztripFlightAddOnsResponse source) {

        if (Boolean.FALSE.equals(isSuccess) || source == null) {
            return FlightAddOnsResponse.builder()
                .journeysWithAvailableAddOnsOptions(List.of())
                .build();
        }

        List<JourneyAddOnsResponse> journeys =
            source.getJourneysWithAvailableAddOnsOptions() == null
                ? List.of()
                : source.getJourneysWithAvailableAddOnsOptions()
                .stream()
                .map(this::mapJourney)
                .toList();

        return FlightAddOnsResponse.builder()
            .journeysWithAvailableAddOnsOptions(journeys)
            .build();
    }

    private JourneyAddOnsResponse mapJourney(BiztripFlightAddOnDataResponse source) {

        if (source == null) {
            return null;
        }

        JourneyAddOnsResponse response = new JourneyAddOnsResponse();
        response.setSegmentsWithAvailableAddOns(source.getSegmentsWithAvailableAddOns());
        response.setAvailableAddOnsOptions(
            mapAvailableAddOnsOptions(source.getAvailableAddOnsOptions())
        );

        return response;
    }

    private AvailableAddOnsOptionsResponse mapAvailableAddOnsOptions(
        BiztripFlightAvailableAddOnsOptions source) {

        if (source == null) {
            return null;
        }

        AvailableAddOnsOptionsResponse response = new AvailableAddOnsOptionsResponse();

        response.setMealOptions(source.getMealOptions());
        response.setBaggageOptions(
            source.getBaggageOptions() == null
                ? List.of()
                : source.getBaggageOptions()
                .stream()
                .map(this::mapBaggageOption)
                .toList()
        );

        return response;
    }

    private BaggageOptionResponse mapBaggageOption(BiztripFlightBaggageOption source) {

        if (source == null) {
            return null;
        }

        BaggageOptionResponse response = new BaggageOptionResponse();
        response.setId(source.getId());
        response.setBaggageType(source.getBaggageType());
        response.setBaggageQuantity(source.getBaggageQuantity());
        response.setBaggageWeight(source.getBaggageWeight());
        response.setPriceWithCurrency(mapPrice(source.getPriceWithCurrency()));
        response.setNetToAgent(mapPrice(source.getNetToAgent()));

        return response;
    }

    private PriceResponse mapPrice(BiztripFlightPrice source) {

        if (source == null) {
            return null;
        }

        PriceResponse response = new PriceResponse();
        response.setAmount(source.getAmount());
        response.setCurrency(source.getCurrency());

        return response;
    }

}
