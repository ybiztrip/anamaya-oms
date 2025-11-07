package ai.anamaya.service.oms.controller;

import ai.anamaya.service.oms.dto.request.HotelRateRequest;
import ai.anamaya.service.oms.dto.request.HotelSearchRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.HotelRateResponse;
import ai.anamaya.service.oms.dto.response.HotelResponse;
import ai.anamaya.service.oms.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotel")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping("/search")
    public ApiResponse<List<HotelResponse>> searchHotels(
            @RequestParam(defaultValue = "biztrip") String source,
            @Valid @RequestBody HotelSearchRequest request
    ) {
        return hotelService.searchHotels(source, request);
    }

    @PostMapping("/rate")
    public ApiResponse<List<HotelRateResponse>> getHotelRates(
            @RequestParam(required = false) String source,
            @Valid @RequestBody HotelRateRequest request
    ) {
        return hotelService.getHotelRates(source, request);
    }

}
