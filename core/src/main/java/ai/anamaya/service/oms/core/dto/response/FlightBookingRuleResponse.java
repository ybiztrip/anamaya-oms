package ai.anamaya.service.oms.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightBookingRuleResponse {
    private Boolean requiresBirthDate;
    private Boolean requiresDocumentNoForInternational;
    private Boolean requiresNationality;
    private Boolean requiresDocumentNoForDomestic;
    private Boolean requiresId;
}
