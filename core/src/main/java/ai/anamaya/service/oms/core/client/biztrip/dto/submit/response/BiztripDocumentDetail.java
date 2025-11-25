package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

@Data
public class BiztripDocumentDetail {
    private String issuingCountry;
    private String documentNo;
    private String expirationDate;
    private String issuanceDate;
    private String documentType;
}
