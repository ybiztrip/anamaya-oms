package ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BiztripHotelPropertyDetailResponse {

    private List<PropertyData> propertyDatas;

    // ================== PROPERTY DATA ==================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropertyData {
        private String status;
        private String propertyId;

        private PropertySummary propertySummary;
        private Object propertyDetail;

        private List<PropertyImage> propertyImages;
        private List<PropertyAmenity> propertyAmenities;

        private CheckInInfo checkInInfo;
        private CheckOutInfo checkOutInfo;
        private FeesInfo feesInfo;
        private PoliciesInfo policiesInfo;
    }

    // ================== SUMMARY ==================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
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

    // ================== ADDRESS ==================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private List<String> lines;
        private String city;
        private String province;
        private String postalCode;
        private String country;
    }

    // ================== GEO ==================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoLocation {
        private String lat;
        private String lon;
    }

    // ================== IMAGES ==================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropertyImage {
        private List<ImageEntry> entries;
        private Boolean main;
        private Boolean isMain;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageEntry {
        private String imageType;
        private String url;
    }

    // ================== AMENITIES ==================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PropertyAmenity {
        private String id;
        private String category;
        private String name;
    }

    // ================== CHECK IN ==================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckInInfo {
        private String instructions;

        @JsonProperty("special_instructions")
        private String specialInstructions;

        @JsonProperty("begin_time")
        private String beginTime;

        @JsonProperty("min_age")
        private Integer minAge;
    }

    // ================== CHECK OUT ==================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckOutInfo {
        private String time;
    }

    // ================== FEES ==================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeesInfo {
        private String optional;
        private String mandatory;
    }

    // ================== POLICIES ==================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PoliciesInfo {

        private String instructions;

        @JsonProperty("know_before_you_go")
        private String knowBeforeYouGo;
    }
}