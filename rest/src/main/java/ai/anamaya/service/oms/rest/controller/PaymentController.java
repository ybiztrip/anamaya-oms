package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.*;
import ai.anamaya.service.oms.core.dto.response.*;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.PaymentService;
import ai.anamaya.service.oms.rest.dto.request.PaymentCCListRequestRest;
import ai.anamaya.service.oms.rest.dto.response.PaymentCCResponseRest;
import ai.anamaya.service.oms.rest.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper mapper;
    private final JwtUtils jwtUtils;

    @GetMapping("/credit-card")
    public ApiResponse<List<PaymentCCResponseRest>> getGeoList(
        @RequestParam(defaultValue = "biztrip") String source,
        @ModelAttribute PaymentCCListRequestRest request
    ) {
        Long companyId = jwtUtils.getCompanyIdFromToken();
        Long userId = jwtUtils.getUserIdFromToken();
        String userEmail = jwtUtils.getEmailFromToken();
        UserCallerContext userCallerContext = new UserCallerContext(companyId, userId, userEmail);

        PaymentCCListRequest reqCore = mapper.toCore(request);
        List<PaymentCCResponse> response = paymentService.getCC(userCallerContext, source, reqCore);

        return ApiResponse.success(
            mapper.toRest(response)
        );
    }

}
