package ai.anamaya.service.oms.core.client.biztrip.mapper.request;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request.BiztripHotelPropertyRateRequest;
import ai.anamaya.service.oms.core.dto.request.HotelPropertyRateRequest;

public class BiztripHotelPropertyRateRequestMapper {

    public BiztripHotelPropertyRateRequest map(HotelPropertyRateRequest request) {

        if (request == null) {
            return null;
        }

        BiztripHotelPropertyRateRequest.BiztripHotelPropertyRateRequestBuilder builder =
            BiztripHotelPropertyRateRequest.builder()
                .propertyIds(request.getPropertyIds())
                .checkInDate(request.getCheckInDate() != null ? request.getCheckInDate().toString() : null)
                .checkOutDate(request.getCheckOutDate() != null ? request.getCheckOutDate().toString() : null)
                .numRooms(request.getNumRooms())
                .numAdults(request.getNumAdults())
                .displayCurrency(request.getDisplayCurrency())
                .sortBy(request.getSortBy());

        if (request.getFilters() != null) {

            BiztripHotelPropertyRateRequest.Filters.FiltersBuilder filtersBuilder =
                BiztripHotelPropertyRateRequest.Filters.builder();

            if (request.getFilters().getStarRating() != null) {
                filtersBuilder.starRating(request.getFilters().getStarRating());
            }

            if (request.getFilters().getPriceRange() != null) {
                HotelPropertyRateRequest.PriceRange pr = request.getFilters().getPriceRange();

                filtersBuilder.priceRange(
                    BiztripHotelPropertyRateRequest.PriceRange.builder()
                        .min(pr.getMin())
                        .max(pr.getMax())
                        .build()
                );
            }

            builder.filters(filtersBuilder.build());
        }

        return builder.build();
    }
}