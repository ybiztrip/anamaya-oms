package ai.anamaya.service.oms.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDiscoveryResponse {

    private String totalProperties;
    private String maximalOffset;
    private List<Property> properties;
    private String nextCursor;
    private String totalPages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Property {

        private String propertyId;
        private PropertySummary propertySummary;
        private List<PropertyImage> propertyImages;
        private String cheapestRoomName;
        private CheapestRoom cheapestRoom;
        private String rateKey;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PropertySummary {
        private String name;
        private String formerName;
        private Address address;
        private String phoneNumber;
        private Address localAddress;
        private String starRating;
        private Double reviewScore;
        private String accommodationType;
        private GeoLocation geoLocation;
        private String countryISO;
        private String geoId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Address {
        private List<String> lines;
        private String city;
        private String province;
        private String postalCode;
        private String country;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GeoLocation {
        private String lat;
        private String lon;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PropertyImage {
        private List<ImageEntry> entries;
        private Boolean main;
        private Boolean isMain;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImageEntry {
        private String imageType;
        private String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CheapestRoom {
        private ChargeableRate chargeableRate;
        private ChargeableRate convertedChargeableRate;
        private String numOfRooms;
        private String roomId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChargeableRate {
        private String currencyCode;
        private String averageBaseRate;
        private String averageRate;
        private String nightlyRateTotal;
        private String surchargeTotal;
        private String total;

        private List<Surcharge> surcharges;
        private List<NightlyRate> nightlyRates;

        private String recommendedSellingPrice;
        private String serviceFeeCharges;
        private String serviceFeeTotal;
        private String chargeableRateInfo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Surcharge {
        private String type;
        private String displayCurrency;
        private Integer displayAmount;
        private Boolean included;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NightlyRate {
        private String date;
        private String baseRate;
        private Boolean promo;
        private String nightRate;
    }

}
