package ai.anamaya.service.oms.core.client.biztrip.mapper.response;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelPropertyDetailResponse;
import ai.anamaya.service.oms.core.dto.response.HotelPropertyDetailResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BiztripHotelPropertyDetailResponseMapper {

    public HotelPropertyDetailResponse map(BiztripHotelPropertyDetailResponse source) {

        if (source == null) {
            return null;
        }

        return HotelPropertyDetailResponse.builder()
            .propertyDatas(mapPropertyDatas(source.getPropertyDatas()))
            .build();
    }

    private List<HotelPropertyDetailResponse.PropertyData> mapPropertyDatas(
        List<BiztripHotelPropertyDetailResponse.PropertyData> source
    ) {
        if (source == null) return null;

        return source.stream().map(item ->
            new HotelPropertyDetailResponse.PropertyData(
                item.getStatus(),
                item.getPropertyId(),
                mapPropertySummary(item.getPropertySummary()),
                item.getPropertyDetail(),
                mapPropertyImages(item.getPropertyImages()),
                mapPropertyAmenities(item.getPropertyAmenities()),
                mapCheckInInfo(item.getCheckInInfo()),
                mapCheckOutInfo(item.getCheckOutInfo()),
                mapFeesInfo(item.getFeesInfo()),
                mapPoliciesInfo(item.getPoliciesInfo())
            )
        ).toList();
    }

    private HotelPropertyDetailResponse.PropertySummary mapPropertySummary(
        BiztripHotelPropertyDetailResponse.PropertySummary source
    ) {
        if (source == null) return null;

        return new HotelPropertyDetailResponse.PropertySummary(
            source.getName(),
            source.getFormerName(),
            mapAddress(source.getAddress()),
            source.getPhoneNumber(),
            mapAddress(source.getLocalAddress()),
            source.getStarRating(),
            source.getReviewScore(),
            source.getAccommodationType(),
            mapGeoLocation(source.getGeoLocation()),
            source.getCountryISO(),
            source.getGeoId()
        );
    }

    private HotelPropertyDetailResponse.Address mapAddress(
        BiztripHotelPropertyDetailResponse.Address source
    ) {
        if (source == null) return null;

        return new HotelPropertyDetailResponse.Address(
            source.getLines(),
            source.getCity(),
            source.getProvince(),
            source.getPostalCode(),
            source.getCountry()
        );
    }

    private HotelPropertyDetailResponse.GeoLocation mapGeoLocation(
        BiztripHotelPropertyDetailResponse.GeoLocation source
    ) {
        if (source == null) return null;

        return new HotelPropertyDetailResponse.GeoLocation(
            source.getLat(),
            source.getLon()
        );
    }

    private List<HotelPropertyDetailResponse.PropertyImage> mapPropertyImages(
        List<BiztripHotelPropertyDetailResponse.PropertyImage> source
    ) {
        if (source == null) return null;

        return source.stream().map(img ->
            new HotelPropertyDetailResponse.PropertyImage(
                mapImageEntries(img.getEntries()),
                img.getMain(),
                img.getIsMain()
            )
        ).toList();
    }

    private List<HotelPropertyDetailResponse.ImageEntry> mapImageEntries(
        List<BiztripHotelPropertyDetailResponse.ImageEntry> source
    ) {
        if (source == null) return null;

        return source.stream().map(entry ->
            new HotelPropertyDetailResponse.ImageEntry(
                entry.getImageType(),
                entry.getUrl()
            )
        ).toList();
    }

    private List<HotelPropertyDetailResponse.PropertyAmenity> mapPropertyAmenities(
        List<BiztripHotelPropertyDetailResponse.PropertyAmenity> source
    ) {
        if (source == null) return null;

        return source.stream().map(a ->
            new HotelPropertyDetailResponse.PropertyAmenity(
                a.getId(),
                a.getCategory(),
                a.getName()
            )
        ).toList();
    }

    private HotelPropertyDetailResponse.CheckInInfo mapCheckInInfo(
        BiztripHotelPropertyDetailResponse.CheckInInfo source
    ) {
        if (source == null) return null;

        return new HotelPropertyDetailResponse.CheckInInfo(
            source.getInstructions(),
            source.getSpecialInstructions(),
            source.getBeginTime(),
            source.getMinAge()
        );
    }

    private HotelPropertyDetailResponse.CheckOutInfo mapCheckOutInfo(
        BiztripHotelPropertyDetailResponse.CheckOutInfo source
    ) {
        if (source == null) return null;

        return new HotelPropertyDetailResponse.CheckOutInfo(
            source.getTime()
        );
    }

    private HotelPropertyDetailResponse.FeesInfo mapFeesInfo(
        BiztripHotelPropertyDetailResponse.FeesInfo source
    ) {
        if (source == null) return null;

        return new HotelPropertyDetailResponse.FeesInfo(
            source.getOptional(),
            source.getMandatory()
        );
    }

    private HotelPropertyDetailResponse.PoliciesInfo mapPoliciesInfo(
        BiztripHotelPropertyDetailResponse.PoliciesInfo source
    ) {
        if (source == null) return null;

        return new HotelPropertyDetailResponse.PoliciesInfo(
            source.getInstructions(),
            source.getKnowBeforeYouGo()
        );
    }
}
