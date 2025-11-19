package ai.anamaya.service.oms.core.dto.response.booking.submit;

import lombok.Data;

import java.util.List;

@Data
public class Journey {
    private List<Segment> segments;
}
