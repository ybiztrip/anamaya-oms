package ai.anamaya.service.oms.rest.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRequestRest {
    private String name;
    private Short status;
    private Long createdBy;
}

