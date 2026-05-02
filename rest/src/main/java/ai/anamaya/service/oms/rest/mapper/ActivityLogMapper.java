package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.response.ActivityLogResponse;
import ai.anamaya.service.oms.rest.dto.response.ActivityLogResponseRest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityLogMapper {

    ActivityLogResponseRest toRest(ActivityLogResponse dto);

}
