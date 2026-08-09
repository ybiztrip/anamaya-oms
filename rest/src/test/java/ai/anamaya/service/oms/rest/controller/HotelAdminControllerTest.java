package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.request.UpdateHotelOpenSearchRequest;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
import ai.anamaya.service.oms.core.dto.response.HotelOpenSearchResponse;
import ai.anamaya.service.oms.core.security.JwtUtils;
import ai.anamaya.service.oms.core.service.HotelAdminService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelAdminControllerTest {

    @Mock
    private HotelAdminService hotelAdminService;

    @Mock
    private JwtUtils jwtUtils;

    private HotelAdminController controller;

    private static final Long COMPANY_ID = 10L;
    private static final Long USER_ID = 20L;
    private static final String USER_EMAIL = "user@test.com";

    private void mockJwt() {
        when(jwtUtils.getCompanyIdFromToken()).thenReturn(COMPANY_ID);
        when(jwtUtils.getUserIdFromToken()).thenReturn(USER_ID);
        when(jwtUtils.getEmailFromToken()).thenReturn(USER_EMAIL);
    }

    private HotelOpenSearchResponse sampleResponse() {
        return HotelOpenSearchResponse.builder()
            .id("9409190")
            .name("Hotel Daisy")
            .star(3)
            .estimationPrice(new BigDecimal("300000"))
            .address("[\"Via Dott. F. Garofoli, 294\"]")
            .province("VR")
            .city("San Giovanni Lupatoto")
            .countryCode("IT")
            .postalCode("37057")
            .latitude(45.396868)
            .longitude(11.025483)
            .rank(286700)
            .accommodationType("INN")
            .build();
    }

    @Test
    void getOpenSearch_forwardsIdAndReturnsSuccessResponse() {
        controller = new HotelAdminController(hotelAdminService, jwtUtils);
        mockJwt();
        when(hotelAdminService.getOpenSearch(any(CallerContext.class), eq("9409190")))
            .thenReturn(sampleResponse());

        ApiResponse<HotelOpenSearchResponse> response = controller.getOpenSearch("9409190");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getId()).isEqualTo("9409190");
        assertThat(response.getData().getAccommodationType()).isEqualTo("INN");

        ArgumentCaptor<CallerContext> callerCaptor = ArgumentCaptor.forClass(CallerContext.class);
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(hotelAdminService).getOpenSearch(callerCaptor.capture(), idCaptor.capture());

        assertThat(idCaptor.getValue()).isEqualTo("9409190");
        assertThat(callerCaptor.getValue().companyId()).isEqualTo(COMPANY_ID);
    }

    @Test
    void updateOpenSearch_forwardsIdAndBody_returnsSuccessResponse() {
        controller = new HotelAdminController(hotelAdminService, jwtUtils);
        mockJwt();
        UpdateHotelOpenSearchRequest request = UpdateHotelOpenSearchRequest.builder()
            .star(3)
            .estimationPrice(new BigDecimal("300000"))
            .build();

        when(hotelAdminService.updateOpenSearch(any(CallerContext.class), eq("9409190"), eq(request)))
            .thenReturn(sampleResponse());

        ApiResponse<HotelOpenSearchResponse> response = controller.updateOpenSearch("9409190", request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getStar()).isEqualTo(3);
        assertThat(response.getData().getEstimationPrice()).isEqualByComparingTo(new BigDecimal("300000"));

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UpdateHotelOpenSearchRequest> bodyCaptor = ArgumentCaptor.forClass(UpdateHotelOpenSearchRequest.class);
        verify(hotelAdminService).updateOpenSearch(any(CallerContext.class), idCaptor.capture(), bodyCaptor.capture());

        assertThat(idCaptor.getValue()).isEqualTo("9409190");
        assertThat(bodyCaptor.getValue().getStar()).isEqualTo(3);
        assertThat(bodyCaptor.getValue().getEstimationPrice()).isEqualByComparingTo(new BigDecimal("300000"));
    }

    @Test
    void updateOpenSearch_requestWithMissingFields_failsBeanValidation() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        UpdateHotelOpenSearchRequest invalid = new UpdateHotelOpenSearchRequest();

        Set<ConstraintViolation<UpdateHotelOpenSearchRequest>> violations = validator.validate(invalid);

        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
            .extracting(Object::toString)
            .containsExactlyInAnyOrder("star", "estimationPrice");
    }
}
