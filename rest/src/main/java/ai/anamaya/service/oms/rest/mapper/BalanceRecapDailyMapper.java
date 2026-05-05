package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.response.BalanceRecapDailyResponse;
import ai.anamaya.service.oms.rest.dto.response.BalanceRecapDailyResponseRest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BalanceRecapDailyMapper {

    BalanceRecapDailyResponseRest toRest(BalanceRecapDailyResponse core);
}