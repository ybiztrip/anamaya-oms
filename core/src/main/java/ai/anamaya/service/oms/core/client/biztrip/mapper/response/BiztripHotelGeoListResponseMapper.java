package ai.anamaya.service.oms.core.client.biztrip.mapper.response;

import ai.anamaya.service.oms.core.client.biztrip.dto.hotel.response.BiztripHotelGetGeoListResponse;
import ai.anamaya.service.oms.core.dto.response.HotelGeoListResponse;

import java.util.List;

public class BiztripHotelGeoListResponseMapper {

    public HotelGeoListResponse map(BiztripHotelGetGeoListResponse source) {

        if (source == null || source.getGeoRegionList() == null) {
            return HotelGeoListResponse.builder()
                .geoRegionList(List.of())
                .build();
        }

        List<HotelGeoListResponse.Geo> geoList =
            source.getGeoRegionList().stream()
                .map(this::mapGeo)
                .toList();

        return HotelGeoListResponse.builder()
            .geoRegionList(geoList)
            .build();
    }

    private HotelGeoListResponse.Geo mapGeo(
        BiztripHotelGetGeoListResponse.Geo g
    ) {
        return HotelGeoListResponse.Geo.builder()
            .geoId(g.getGeoId())
            .parentId(g.getParentId())
            .type(g.getType())
            .name(g.getName())
            .localeName(g.getLocaleName())
            .centroId(mapCentro(g.getCentroid()))
            .build();
    }

    private HotelGeoListResponse.Centro mapCentro(
        BiztripHotelGetGeoListResponse.Centro c
    ) {
        if (c == null) {
            return null;
        }

        return HotelGeoListResponse.Centro.builder()
            .lon(c.getLon())
            .lat(c.getLat())
            .valid(c.getValid())
            .build();
    }
}
