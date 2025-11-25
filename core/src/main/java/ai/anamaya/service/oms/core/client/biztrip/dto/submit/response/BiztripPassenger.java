package ai.anamaya.service.oms.core.client.biztrip.dto.submit.response;

import lombok.Data;

import java.util.List;

@Data
public class BiztripPassenger {
    private String title;
    private String firstName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private BiztripDocumentDetail documentDetail;
    private String nationality;
    private String birthLocation;
    private List<BiztripAddOn> addOns;
}
