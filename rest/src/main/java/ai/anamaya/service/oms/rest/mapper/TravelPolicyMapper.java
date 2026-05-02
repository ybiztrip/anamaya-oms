package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.*;
import ai.anamaya.service.oms.core.dto.response.TravelPolicyResponse;
import ai.anamaya.service.oms.rest.dto.request.*;
import ai.anamaya.service.oms.rest.dto.response.TravelPolicyResponseRest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TravelPolicyMapper {

    TravelPolicyRequest toCore(TravelPolicyRequestRest dto);
    TravelPolicyResponseRest toRest(TravelPolicyResponse dto);

}
