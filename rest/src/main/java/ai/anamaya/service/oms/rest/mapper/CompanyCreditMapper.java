package ai.anamaya.service.oms.rest.mapper;

import ai.anamaya.service.oms.core.dto.request.CompanyCreditInvoiceRequest;
import ai.anamaya.service.oms.core.dto.response.CompanyCreditInvoiceResponse;
import ai.anamaya.service.oms.core.dto.response.CompanyCreditResponse;
import ai.anamaya.service.oms.rest.dto.request.CompanyCreditInvoiceRequestRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyCreditInvoiceResponseRest;
import ai.anamaya.service.oms.rest.dto.response.CompanyCreditResponseRest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyCreditMapper {

    // Requests
    CompanyCreditInvoiceRequest toCore(CompanyCreditInvoiceRequestRest rest);

    // Responses
    CompanyCreditResponseRest toRest(CompanyCreditResponse core);
    CompanyCreditInvoiceResponseRest toRest(CompanyCreditInvoiceResponse core);
}
