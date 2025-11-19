package ai.anamaya.service.oms.core.dto.request.booking.submit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentDetail {
    private String issuingCountry;
    private String documentNo;
    private String expirationDate;
    private String documentType;
}
