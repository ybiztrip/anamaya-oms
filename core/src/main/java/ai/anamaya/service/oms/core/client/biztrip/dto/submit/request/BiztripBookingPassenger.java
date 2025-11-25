package ai.anamaya.service.oms.core.client.biztrip.dto.submit.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BiztripBookingPassenger {
    private String title;
    private String firstName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String nationality;
    private BiztripBookingDocumentDetail documentDetail;
    private List<Object> addOns;
}
