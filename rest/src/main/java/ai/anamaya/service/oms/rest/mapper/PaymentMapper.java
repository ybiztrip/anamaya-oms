package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.PaymentCCListRequest;
import ai.anamaya.service.oms.core.dto.response.PaymentCCResponse;
import ai.anamaya.service.oms.rest.dto.request.PaymentCCListRequestRest;
import ai.anamaya.service.oms.rest.dto.response.PaymentCCResponseRest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentCCListRequest toCore(PaymentCCListRequestRest dto);
    List<PaymentCCResponseRest> toRest(List<PaymentCCResponse> core);

}
