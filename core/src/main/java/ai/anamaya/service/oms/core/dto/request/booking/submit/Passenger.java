package ai.anamaya.service.oms.core.dto.request.booking.submit;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Passenger {
    private String title;
    private String firstName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String nationality;
    private DocumentDetail documentDetail;
    private List<Object> addOns;
}
