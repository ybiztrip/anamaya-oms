package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.HotelGeoListRequest;
import ai.anamaya.service.oms.core.dto.response.HotelGeoListResponse;
import ai.anamaya.service.oms.rest.dto.request.HotelGeoListRequestRest;
import ai.anamaya.service.oms.rest.dto.response.HotelGeoListResponseRest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    HotelGeoListRequest toCore(HotelGeoListRequestRest dto);
    HotelGeoListResponseRest toRest(HotelGeoListResponse core);

}
