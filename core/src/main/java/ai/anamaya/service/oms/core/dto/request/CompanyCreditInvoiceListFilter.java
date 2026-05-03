package ai.anamaya.service.oms.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyCreditInvoiceListFilter {
    private int page;
    private int size;
    private String sort;
    private Long companyId;
}
