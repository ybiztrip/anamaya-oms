package ai.anamaya.service.oms.core.client.biztrip.mapper.request;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.request.BiztripHotelDiscoveryRequest;
import ai.anamaya.service.oms.core.dto.request.HotelDiscoveryRequest;

public class BiztripHotelDiscoveryRequestMapper {

    public BiztripHotelDiscoveryRequest map(HotelDiscoveryRequest request) {

        if (request == null) {
            return null;
        }

        BiztripHotelDiscoveryRequest.BiztripHotelDiscoveryRequestBuilder builder =
            BiztripHotelDiscoveryRequest.builder()
                .geoId(request.getGeoId())
                .checkInDate(String.valueOf(request.getCheckInDate()))
                .checkOutDate(String.valueOf(request.getCheckOutDate()))
                .numRooms(request.getNumRooms())
                .displayCurrency(request.getDisplayCurrency())
                .sortBy(request.getSortBy())
                .page(request.getPage())
                .limit(request.getLimit());

        if (request.getFilters() != null) {

            BiztripHotelDiscoveryRequest.Filters.FiltersBuilder filtersBuilder =
                BiztripHotelDiscoveryRequest.Filters.builder();

            if (request.getFilters().getStarRating() != null) {
                filtersBuilder.starRating(request.getFilters().getStarRating());
            }

            if (request.getFilters().getPriceRange() != null) {
                HotelDiscoveryRequest.priceRange pr =
                    request.getFilters().getPriceRange();

                filtersBuilder.priceRange(
                    new BiztripHotelDiscoveryRequest.PriceRange(
                        pr.getMin(),
                        pr.getMax()
                    )
                );
            }

            builder.filters(filtersBuilder.build());
        }

        return builder.build();
    }

}
