package ai.anamaya.service.oms.controller;

import ai.anamaya.service.oms.dto.request.BiztripHotelSearchRequest;
import ai.anamaya.service.oms.dto.response.ApiResponse;
import ai.anamaya.service.oms.dto.response.BiztripHotelResponse;
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
    public ApiResponse<List<BiztripHotelResponse>> searchHotels(
            @RequestParam(defaultValue = "biztrip") String source,
            @Valid @RequestBody BiztripHotelSearchRequest request
    ) {
        return hotelService.searchHotels(source, request);
    }
}
