package ai.anamaya.service.oms.rest.controller;

import ai.anamaya.service.oms.core.client.internal.dto.request.PropertyMappingRequest;
import ai.anamaya.service.oms.core.client.internal.dto.response.HotelPropertyMappingResponse;
import ai.anamaya.service.oms.core.client.internal.dto.response.HotelPropertyMappingUpdateResponse;
import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.dto.response.ApiResponse;
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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelPropertyMappingControllerTest {

    @Mock
    private HotelAdminService hotelAdminService;

    @Mock
    private JwtUtils jwtUtils;

    private HotelPropertyMappingController controller;

    private static final Long COMPANY_ID = 10L;
    private static final Long USER_ID = 20L;
    private static final String USER_EMAIL = "user@test.com";

    private void mockJwt() {
        when(jwtUtils.getCompanyIdFromToken()).thenReturn(COMPANY_ID);
        when(jwtUtils.getUserIdFromToken()).thenReturn(USER_ID);
        when(jwtUtils.getEmailFromToken()).thenReturn(USER_EMAIL);
    }

    private HotelPropertyMappingResponse sampleMapping() {
        return HotelPropertyMappingResponse.builder()
            .id(428433L)
            .propertyId(9409190L)
            .providerPropertyId("100567384")
            .providerAliasName("Hotel Daisy")
            .provider("EXPEDIA")
            .status(null)
            .createdOn(null)
            .updatedOn(1764569045000L)
            .build();
    }

    @Test
    void getPropertyMapping_forwardsIdAndReturnsSuccessResponse() {
        controller = new HotelPropertyMappingController(hotelAdminService, jwtUtils);
        mockJwt();
        when(hotelAdminService.getPropertyMapping(any(CallerContext.class), eq("9409190")))
            .thenReturn(List.of(sampleMapping()));

        ApiResponse<List<HotelPropertyMappingResponse>> response = controller.getPropertyMapping("9409190");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getData().get(0).getProviderPropertyId()).isEqualTo("100567384");

        ArgumentCaptor<CallerContext> callerCaptor = ArgumentCaptor.forClass(CallerContext.class);
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(hotelAdminService).getPropertyMapping(callerCaptor.capture(), idCaptor.capture());

        assertThat(idCaptor.getValue()).isEqualTo("9409190");
        assertThat(callerCaptor.getValue().companyId()).isEqualTo(COMPANY_ID);
    }

    @Test
    void getPropertyMapping_emptyList_returnsSuccessWithEmptyData() {
        controller = new HotelPropertyMappingController(hotelAdminService, jwtUtils);
        mockJwt();
        when(hotelAdminService.getPropertyMapping(any(CallerContext.class), eq("9409190")))
            .thenReturn(Collections.emptyList());

        ApiResponse<List<HotelPropertyMappingResponse>> response = controller.getPropertyMapping("9409190");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEmpty();
    }

    @Test
    void updatePropertyMapping_forwardsIdAndBody_returnsSuccessResponse() {
        controller = new HotelPropertyMappingController(hotelAdminService, jwtUtils);
        mockJwt();
        List<PropertyMappingRequest> request = List.of(
            PropertyMappingRequest.builder()
                .provider("EXPEDIA")
                .providerPropertyId(100567384L)
                .providerAliasName("Hotel Daisy")
                .build(),
            PropertyMappingRequest.builder()
                .provider("EXPEDIA")
                .providerPropertyId(91425335L)
                .providerAliasName("Hotel Daisy")
                .build()
        );

        when(hotelAdminService.updatePropertyMapping(any(CallerContext.class), eq("9409190"), eq(request)))
            .thenReturn(HotelPropertyMappingUpdateResponse.builder().updatedCount(2).build());

        ApiResponse<HotelPropertyMappingUpdateResponse> response = controller.updatePropertyMapping("9409190", request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getUpdatedCount()).isEqualTo(2);

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<PropertyMappingRequest>> bodyCaptor = ArgumentCaptor.forClass(List.class);
        verify(hotelAdminService).updatePropertyMapping(any(CallerContext.class), idCaptor.capture(), bodyCaptor.capture());

        assertThat(idCaptor.getValue()).isEqualTo("9409190");
        assertThat(bodyCaptor.getValue()).extracting(PropertyMappingRequest::getProviderPropertyId)
            .containsExactly(100567384L, 91425335L);
        assertThat(bodyCaptor.getValue()).allSatisfy(item -> assertThat(item.getProviderAliasName()).isEqualTo("Hotel Daisy"));
    }

    @Test
    void updatePropertyMapping_withoutProviderAliasName_passesBeanValidation() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        PropertyMappingRequest valid = PropertyMappingRequest.builder()
            .provider("EXPEDIA")
            .providerPropertyId(100567384L)
            .build();

        Set<ConstraintViolation<PropertyMappingRequest>> violations = validator.validate(valid);

        assertThat(violations).isEmpty();
    }

    @Test
    void updatePropertyMapping_missingProviderPropertyId_failsBeanValidation() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        PropertyMappingRequest invalid = PropertyMappingRequest.builder()
            .provider("EXPEDIA")
            .build();

        Set<ConstraintViolation<PropertyMappingRequest>> violations = validator.validate(invalid);

        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
            .extracting(Object::toString)
            .containsExactly("providerPropertyId");
    }

    @Test
    void updatePropertyMapping_emptyRequest_failsBeanValidation() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        PropertyMappingRequest invalid = new PropertyMappingRequest();

        Set<ConstraintViolation<PropertyMappingRequest>> violations = validator.validate(invalid);

        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
            .extracting(Object::toString)
            .containsExactlyInAnyOrder("provider", "providerPropertyId");
    }
}
