package ai.anamaya.service.oms.core.client.biztrip.dto.submit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiztripBookingDocumentDetail {
    private String issuingCountry;
    private String documentNo;
    private String expirationDate;
    private String documentType;
}
