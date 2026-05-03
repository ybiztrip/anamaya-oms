package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.CompanyCreditInvoiceRequest;
import ai.anamaya.service.oms.core.dto.response.CompanyCreditInvoiceResponse;
import ai.anamaya.service.oms.rest.dto.request.CompanyCreditInvoiceRequestRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyCreditInvoiceResponseRest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyCreditMapper {

    // Requests
    CompanyCreditInvoiceRequest toCore(CompanyCreditInvoiceRequestRest rest);

    // Responses
    CompanyCreditInvoiceResponseRest toRest(CompanyCreditInvoiceResponse core);
}
