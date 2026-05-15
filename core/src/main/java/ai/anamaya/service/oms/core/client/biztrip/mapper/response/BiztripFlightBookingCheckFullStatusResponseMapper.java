package ai.anamaya.service.oms.core.client.biztrip.mapper.response;

import ai.anamaya.service.oms.core.client.biztrip.dto.submit.response.*;
import ai.anamaya.service.oms.core.dto.response.booking.submit.*;

import java.util.List;


public class BiztripFlightBookingCheckFullStatusResponseMapper {

    public BookingFlightSubmitResponse map(BiztripCheckFullStatusResponse b) {
        BookingFlightSubmitResponse res = new BookingFlightSubmitResponse();

        if(b.getBookingStatusResult() == null || b.getBookingStatusResult().isEmpty()) {
            return null;
        }

        BiztripCheckFullStatusResult statusResult = b.getBookingStatusResult().get(0);
        res.setBookingSubmissionStatus(statusResult.getStatus());
        res.setBookingId(statusResult.getBookingId());
        res.setPnrInfo(toPnrInfo(statusResult.getPnrInfo()));
        return res;
    }

    private BookingFlightSubmitResponse.PnrInfo toPnrInfo(BiztripCheckFullStatusResult.PnrInfo src) {
        if (src == null) {
            return null;
        }
        BookingFlightSubmitResponse.PnrInfo dst = new BookingFlightSubmitResponse.PnrInfo();
        dst.setDeparturePnr(toPnrDataList(src.getDeparturePnr()));
        dst.setReturnPnr(toPnrDataList(src.getReturnPnr()));
        return dst;
    }

    private List<BookingFlightSubmitResponse.PnrData> toPnrDataList(List<BiztripCheckFullStatusResult.PnrData> src) {
        if (src == null) {
            return null;
        }
        return src.stream().map(this::toPnrData).toList();
    }

    private BookingFlightSubmitResponse.PnrData toPnrData(BiztripCheckFullStatusResult.PnrData src) {
        BookingFlightSubmitResponse.PnrData dst = new BookingFlightSubmitResponse.PnrData();
        dst.setProviderPnr(src.getProviderPnr());
        if (src.getAirlinePnrItems() != null) {
            dst.setAirlinePnrItems(src.getAirlinePnrItems().stream().map(item -> {
                BookingFlightSubmitResponse.AirlinePnrItems dstItem = new BookingFlightSubmitResponse.AirlinePnrItems();
                dstItem.setAirlinePnr(item.getAirlinePnr());
                dstItem.setSegment(item.getSegment());
                return dstItem;
            }).toList());
        }
        return dst;
    }

}
