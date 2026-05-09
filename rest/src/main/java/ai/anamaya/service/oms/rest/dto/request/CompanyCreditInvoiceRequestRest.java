package ai.anamaya.service.oms.rest.dto.request;

import ai.anamaya.service.oms.core.enums.InvoiceProductType;
import lombok.Data;

import java.util.List;

@Data
public class CompanyCreditInvoiceRequestRest {
    private Long companyId;
    private String docNo;
    private InvoiceProductType type;
    private List<Long> bookingFlightIds;
    private List<Long> bookingHotelIds;
}
