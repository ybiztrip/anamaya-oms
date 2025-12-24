package ai.anamaya.service.oms.core.client.biztrip.mapper.response;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelDiscoveryResponse;
import ai.anamaya.service.oms.core.dto.response.HotelDiscoveryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BiztripHotelDiscoveryResponseMapper {

    public HotelDiscoveryResponse map(BiztripHotelDiscoveryResponse source) {

        if (source == null) {
            return null;
        }

        return HotelDiscoveryResponse.builder()
            .totalProperties(source.getTotalProperties())
            .maximalOffset(source.getMaximalOffset())
            .nextCursor(source.getNextCursor())
            .totalPages(source.getTotalPages())
            .properties(mapProperties(source.getProperties()))
            .build();
    }

    private List<HotelDiscoveryResponse.Property> mapProperties(
        List<BiztripHotelDiscoveryResponse.Property> properties) {

        if (properties == null) {
            return List.of();
        }

        return properties.stream()
            .map(this::mapProperty)
            .toList();
    }

    private HotelDiscoveryResponse.Property mapProperty(
        BiztripHotelDiscoveryResponse.Property source) {

        if (source == null) {
            return null;
        }

        return HotelDiscoveryResponse.Property.builder()
            .propertyId(source.getPropertyId())
            .propertySummary(mapPropertySummary(source.getPropertySummary()))
            .propertyImages(mapPropertyImages(source.getPropertyImages()))
            .cheapestRoomName(source.getCheapestRoomName())
            .cheapestRoom(mapCheapestRoom(source.getCheapestRoom()))
            .rateKey(source.getRateKey())
            .build();
    }

    private HotelDiscoveryResponse.PropertySummary mapPropertySummary(
        BiztripHotelDiscoveryResponse.PropertySummary source) {

        if (source == null) {
            return null;
        }

        return HotelDiscoveryResponse.PropertySummary.builder()
            .name(source.getName())
            .formerName(source.getFormerName())
            .address(mapAddress(source.getAddress()))
            .phoneNumber(source.getPhoneNumber())
            .localAddress(mapAddress(source.getLocalAddress()))
            .starRating(source.getStarRating())
            .reviewScore(source.getReviewScore())
            .accommodationType(source.getAccommodationType())
            .geoLocation(mapGeoLocation(source.getGeoLocation()))
            .countryISO(source.getCountryISO())
            .geoId(source.getGeoId())
            .build();
    }

    private HotelDiscoveryResponse.Address mapAddress(
        BiztripHotelDiscoveryResponse.Address source) {

        if (source == null) {
            return null;
        }

        return HotelDiscoveryResponse.Address.builder()
            .lines(source.getLines())
            .city(source.getCity())
            .province(source.getProvince())
            .postalCode(source.getPostalCode())
            .country(source.getCountry())
            .build();
    }

    private HotelDiscoveryResponse.GeoLocation mapGeoLocation(
        BiztripHotelDiscoveryResponse.GeoLocation source) {

        if (source == null) {
            return null;
        }

        return HotelDiscoveryResponse.GeoLocation.builder()
            .lat(source.getLat())
            .lon(source.getLon())
            .build();
    }

    private List<HotelDiscoveryResponse.PropertyImage> mapPropertyImages(
        List<BiztripHotelDiscoveryResponse.PropertyImage> images) {

        if (images == null) {
            return List.of();
        }

        return images.stream()
            .map(this::mapPropertyImage)
            .toList();
    }

    private HotelDiscoveryResponse.PropertyImage mapPropertyImage(
        BiztripHotelDiscoveryResponse.PropertyImage source) {

        if (source == null) {
            return null;
        }

        return HotelDiscoveryResponse.PropertyImage.builder()
            .entries(mapImageEntries(source.getEntries()))
            .main(source.getMain())
            .isMain(source.getIsMain())
            .build();
    }

    private List<HotelDiscoveryResponse.ImageEntry> mapImageEntries(
        List<BiztripHotelDiscoveryResponse.ImageEntry> entries) {

        if (entries == null) {
            return List.of();
        }

        return entries.stream()
            .map(e -> HotelDiscoveryResponse.ImageEntry.builder()
                .imageType(e.getImageType())
                .url(e.getUrl())
                .build())
            .toList();
    }

    private HotelDiscoveryResponse.CheapestRoom mapCheapestRoom(
        BiztripHotelDiscoveryResponse.CheapestRoom source) {

        if (source == null) {
            return null;
        }

        return HotelDiscoveryResponse.CheapestRoom.builder()
            .chargeableRate(mapChargeableRate(source.getChargeableRate()))
            .convertedChargeableRate(mapChargeableRate(source.getConvertedChargeableRate()))
            .numOfRooms(source.getNumOfRooms())
            .roomId(source.getRoomId())
            .build();
    }

    private HotelDiscoveryResponse.ChargeableRate mapChargeableRate(
        BiztripHotelDiscoveryResponse.ChargeableRate source) {

        if (source == null) {
            return null;
        }

        return HotelDiscoveryResponse.ChargeableRate.builder()
            .currencyCode(source.getCurrencyCode())
            .averageBaseRate(source.getAverageBaseRate())
            .averageRate(source.getAverageRate())
            .nightlyRateTotal(source.getNightlyRateTotal())
            .surchargeTotal(source.getSurchargeTotal())
            .total(source.getTotal())
            .surcharges(mapSurcharges(source.getSurcharges()))
            .nightlyRates(mapNightlyRates(source.getNightlyRates()))
            .recommendedSellingPrice(source.getRecommendedSellingPrice())
            .serviceFeeCharges(source.getServiceFeeCharges())
            .serviceFeeTotal(source.getServiceFeeTotal())
            .chargeableRateInfo(source.getChargeableRateInfo())
            .build();
    }

    private List<HotelDiscoveryResponse.Surcharge> mapSurcharges(
        List<BiztripHotelDiscoveryResponse.Surcharge> surcharges) {

        if (surcharges == null) {
            return List.of();
        }

        return surcharges.stream()
            .map(s -> HotelDiscoveryResponse.Surcharge.builder()
                .type(s.getType())
                .displayCurrency(s.getDisplayCurrency())
                .displayAmount(s.getDisplayAmount())
                .included(s.getIncluded())
                .build())
            .toList();
    }

    private List<HotelDiscoveryResponse.NightlyRate> mapNightlyRates(
        List<BiztripHotelDiscoveryResponse.NightlyRate> nightlyRates) {

        if (nightlyRates == null) {
            return List.of();
        }

        return nightlyRates.stream()
            .map(n -> HotelDiscoveryResponse.NightlyRate.builder()
                .date(n.getDate())
                .baseRate(n.getBaseRate())
                .promo(n.getPromo())
                .nightRate(n.getNightRate())
                .build())
            .toList();
    }
}
