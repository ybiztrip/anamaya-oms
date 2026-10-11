package ai.anamaya.service.oms.core.enums;

import ai.anamaya.service.oms.core.client.biztrip.dto.train.BiztripTrainBookingData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingTrainStatusTest {

    @Test
    void mapsBiztripStatus() {
        assertEquals(BookingTrainStatus.ISSUED, BookingTrainStatus.fromPartnerStatus("Ticketed", BookingTrainStatus.ISSUING));
        assertEquals(BookingTrainStatus.CANCELLED, BookingTrainStatus.fromPartnerStatus("Expired", BookingTrainStatus.ISSUING));
        assertEquals(BookingTrainStatus.ISSUING, BookingTrainStatus.fromPartnerStatus("Reserved", BookingTrainStatus.ISSUING));
        assertEquals(BookingTrainStatus.ISSUING, BookingTrainStatus.fromPartnerStatus(null, BookingTrainStatus.ISSUING));
    }

    @Test
    void readsBookAndDetailCasing() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String book = """
            {"id":"a","bookingCode":"QKV9REJ","status":"Reserved","passengers":[{"index":1,"seatNumber":"EKS-1/1C"}],
             "payments":[{"Code":"TOTAL","Amount":1},{"Code":"TOTAL_PAID","Amount":49500}]}""";
        String detail = """
            {"Id":"a","BookingCode":"QKV9REJ","Status":"Ticketed","Passengers":[{"Index":1,"SeatNumber":"EKS-1/1B"}]}""";

        BiztripTrainBookingData b = mapper.readValue(book, BiztripTrainBookingData.class);
        BiztripTrainBookingData d = mapper.readValue(detail, BiztripTrainBookingData.class);

        assertEquals(new BigDecimal("49500"), b.totalPaid());
        assertEquals("EKS-1/1C", b.getPassengers().get(0).getSeatNumber());
        assertEquals("Ticketed", d.getStatus());
        assertEquals("EKS-1/1B", d.getPassengers().get(0).getSeatNumber());
    }
}
