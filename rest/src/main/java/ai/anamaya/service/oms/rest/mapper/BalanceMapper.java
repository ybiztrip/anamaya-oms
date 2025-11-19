package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.BalanceAdjustRequest;
import ai.anamaya.service.oms.core.dto.request.BalanceTopUpRequest;
import ai.anamaya.service.oms.core.dto.response.CompanyBalanceDetailResponse;
import ai.anamaya.service.oms.core.dto.response.CompanyBalanceResponse;
import ai.anamaya.service.oms.rest.dto.request.BalanceAdjustRequestRest;
import ai.anamaya.service.oms.rest.dto.request.BalanceTopUpRequestRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyBalanceDetailResponseRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyBalanceResponseRest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BalanceMapper {

    // Requests
    BalanceAdjustRequest toCore(BalanceAdjustRequestRest rest);
    BalanceTopUpRequest toCore(BalanceTopUpRequestRest rest);

    // Responses
    CompanyBalanceResponseRest toRest(CompanyBalanceResponse core);
    CompanyBalanceDetailResponseRest toRest(CompanyBalanceDetailResponse core);
}
