package ai.anamaya.service.oms.client.biztrip.dto.submit;

import lombok.Data;

@Data
public class BiztripDocumentDetail {
    private String issuingCountry;
    private String documentNo;
    private String expirationDate;
    private String issuanceDate;
    private String documentType;
}
