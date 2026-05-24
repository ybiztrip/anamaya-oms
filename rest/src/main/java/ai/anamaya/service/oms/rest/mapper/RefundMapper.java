package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.RefundCancelRequest;
import ai.anamaya.service.oms.core.dto.request.RefundCreateRequest;
import ai.anamaya.service.oms.core.dto.request.RefundPaidRequest;
import ai.anamaya.service.oms.core.dto.response.RefundResponse;
import ai.anamaya.service.oms.rest.dto.request.RefundCancelRequestRest;
import ai.anamaya.service.oms.rest.dto.request.RefundCreateRequestRest;
import ai.anamaya.service.oms.rest.dto.request.RefundPaidRequestRest;
import ai.anamaya.service.oms.rest.dto.response.RefundResponseRest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefundMapper {

    RefundCreateRequest toCore(RefundCreateRequestRest rest);

    RefundPaidRequest toCore(RefundPaidRequestRest rest);

    RefundCancelRequest toCore(RefundCancelRequestRest rest);

    RefundResponseRest toRest(RefundResponse core);
}
