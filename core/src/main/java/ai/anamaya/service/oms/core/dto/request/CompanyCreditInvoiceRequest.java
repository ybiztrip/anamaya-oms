package ai.anamaya.service.oms.core.dto.request;

import ai.anamaya.service.oms.core.enums.InvoiceProductType;
import lombok.Data;

import java.util.List;

@Data
public class CompanyCreditInvoiceRequest {
    private Long companyId;
    private String docNo;
    private InvoiceProductType type;
    private List<Long> bookingFlightIds;
    private List<Long> bookingHotelIds;
}
