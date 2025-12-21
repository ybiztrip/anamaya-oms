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
public class HotelGeoListResponse {

    private List<Geo> geoRegionList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Geo {
        private String geoId;
        private String parentId;
        private String type;
        private String name;
        private String localeName;
        private Centro centroId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Centro {
        private String lon;
        private String lat;
        private Boolean valid;
    }

}
