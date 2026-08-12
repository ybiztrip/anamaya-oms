package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.PropertyMappingRequest;
import ai.anamaya.service.oms.core.dto.response.HotelPropertyMappingResponse;
import ai.anamaya.service.oms.core.dto.response.HotelPropertyMappingUpdateResponse;
import ai.anamaya.service.oms.core.exception.BiztripIntegrationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BiztripHotelPropertyMappingServiceTest {

    private static final String PATH = "/hotel/admin/property-mapping/{id}";
    private static final String TOKEN = "Bearer test-token";

    @Mock
    private WebClient webClient;

    @Mock
    private BiztripAuthService authService;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> getUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec<?> getHeadersSpec;
    @Mock
    private WebClient.ResponseSpec getResponseSpec;

    @Mock
    private WebClient.RequestBodyUriSpec postUriSpec;
    @Mock
    private WebClient.RequestBodySpec postBodySpec;
    @Mock
    private WebClient.RequestHeadersSpec<?> postHeadersSpec;
    @Mock
    private WebClient.ResponseSpec postResponseSpec;

    private final CallerContext callerContext = new UserCallerContext(1L, 2L, "user@test.com");

    private BiztripHotelPropertyMappingService service;

    @BeforeEach
    void setUp() {
        service = new BiztripHotelPropertyMappingService(webClient, authService, new ObjectMapper());
    }

    @SuppressWarnings("unchecked")
    private void stubGet(Mono<String> response) {
        when(authService.getAccessToken(1L)).thenReturn(TOKEN);
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) getUriSpec);
        when(getUriSpec.uri(eq(PATH), any(Object[].class))).thenReturn((WebClient.RequestHeadersSpec) getHeadersSpec);
        when(getHeadersSpec.header(eq(HttpHeaders.AUTHORIZATION), eq(TOKEN))).thenReturn((WebClient.RequestHeadersSpec) getHeadersSpec);
        when(getHeadersSpec.retrieve()).thenReturn(getResponseSpec);
        when(getResponseSpec.bodyToMono(String.class)).thenReturn(response);
    }

    @SuppressWarnings("unchecked")
    private void stubPost(Mono<String> response) {
        when(authService.getAccessToken(1L)).thenReturn(TOKEN);
        when(webClient.post()).thenReturn(postUriSpec);
        when(postUriSpec.uri(eq(PATH), any(Object[].class))).thenReturn(postBodySpec);
        when(postBodySpec.header(eq(HttpHeaders.AUTHORIZATION), eq(TOKEN))).thenReturn(postBodySpec);
        when(postBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(postBodySpec);
        when(postBodySpec.bodyValue(any())).thenReturn((WebClient.RequestHeadersSpec) postHeadersSpec);
        when(postHeadersSpec.retrieve()).thenReturn(postResponseSpec);
        when(postResponseSpec.bodyToMono(String.class)).thenReturn(response);
    }

    private static String successListJson() {
        return """
            {
              "success": true,
              "data": [
                {
                  "id": 428433,
                  "propertyId": 9409190,
                  "providerPropertyId": "100567384",
                  "providerAliasName": "Hotel Daisy",
                  "provider": "EXPEDIA",
                  "status": null,
                  "createdOn": null,
                  "updatedOn": 1764569045000
                }
              ]
            }
            """;
    }

    private static String successEmptyListJson() {
        return """
            {
              "success": true,
              "data": []
            }
            """;
    }

    private static String successUpdateJson() {
        return """
            {
              "success": true,
              "data": {
                "updatedCount": 2
              }
            }
            """;
    }

    @SuppressWarnings("unchecked")
    @Test
    void getPropertyMapping_callsBiztripAndReturnsMappedList() {
        stubGet(Mono.just(successListJson()));

        List<HotelPropertyMappingResponse> result = service.getPropertyMapping(callerContext, "9409190");

        assertThat(result).hasSize(1);
        HotelPropertyMappingResponse item = result.get(0);
        assertThat(item.getId()).isEqualTo(428433L);
        assertThat(item.getPropertyId()).isEqualTo(9409190L);
        assertThat(item.getProviderPropertyId()).isEqualTo("100567384");
        assertThat(item.getProviderAliasName()).isEqualTo("Hotel Daisy");
        assertThat(item.getProvider()).isEqualTo("EXPEDIA");
        assertThat(item.getStatus()).isNull();
        assertThat(item.getCreatedOn()).isNull();
        assertThat(item.getUpdatedOn()).isEqualTo(1764569045000L);

        ArgumentCaptor<Object[]> idCaptor = ArgumentCaptor.forClass(Object[].class);
        verify(getUriSpec).uri(eq(PATH), idCaptor.capture());
        assertThat(idCaptor.getValue()).containsExactly("9409190");
    }

    @Test
    void getPropertyMapping_emptyDataList_returnsEmptyList() {
        stubGet(Mono.just(successEmptyListJson()));

        List<HotelPropertyMappingResponse> result = service.getPropertyMapping(callerContext, "9409190");

        assertThat(result).isEmpty();
    }

    @Test
    void getPropertyMapping_biztrip404_throwsNotFoundIntegrationException() {
        WebClientResponseException notFound = WebClientResponseException.create(
            404, "Not Found", new HttpHeaders(), new byte[0], null);
        stubGet(Mono.error(notFound));

        assertThatThrownBy(() -> service.getPropertyMapping(callerContext, "unknown-id"))
            .isInstanceOf(BiztripIntegrationException.class)
            .satisfies(ex -> assertThat(((BiztripIntegrationException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void getPropertyMapping_biztrip5xx_throwsIntegrationException() {
        WebClientResponseException serverError = WebClientResponseException.create(
            500, "Internal Server Error", new HttpHeaders(), new byte[0], null);
        stubGet(Mono.error(serverError));

        assertThatThrownBy(() -> service.getPropertyMapping(callerContext, "9409190"))
            .isInstanceOf(BiztripIntegrationException.class)
            .satisfies(ex -> assertThat(((BiztripIntegrationException) ex).getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY));
    }

    @Test
    void getPropertyMapping_connectionFailure_throwsServiceUnavailable() {
        WebClientRequestException connectionFailure = new WebClientRequestException(
            new IOException("Connection refused"), HttpMethod.GET,
            URI.create("http://biztrip.test/hotel/admin/property-mapping/9409190"), new HttpHeaders());
        stubGet(Mono.error(connectionFailure));

        assertThatThrownBy(() -> service.getPropertyMapping(callerContext, "9409190"))
            .isInstanceOf(BiztripIntegrationException.class)
            .satisfies(ex -> assertThat(((BiztripIntegrationException) ex).getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE));
    }

    @SuppressWarnings("unchecked")
    @Test
    void updatePropertyMapping_forwardsIdAndBody_returnsUpdatedCount() {
        stubPost(Mono.just(successUpdateJson()));

        PropertyMappingRequest request = PropertyMappingRequest.builder()
            .providerPropertyId(List.of(100567384L, 91425335L))
            .providerAliasName("Hotel Daisy")
            .build();

        HotelPropertyMappingUpdateResponse result = service.updatePropertyMapping(callerContext, "9409190", request);

        assertThat(result.getUpdatedCount()).isEqualTo(2);

        ArgumentCaptor<Object[]> idCaptor = ArgumentCaptor.forClass(Object[].class);
        verify(postUriSpec).uri(eq(PATH), idCaptor.capture());
        assertThat(idCaptor.getValue()).containsExactly("9409190");

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(postBodySpec).bodyValue(bodyCaptor.capture());
        PropertyMappingRequest forwarded = (PropertyMappingRequest) bodyCaptor.getValue();
        assertThat(forwarded.getProviderPropertyId()).containsExactly(100567384L, 91425335L);
        assertThat(forwarded.getProviderAliasName()).isEqualTo("Hotel Daisy");
        assertThat(forwarded).isSameAs(request);
    }

    @Test
    void updatePropertyMapping_biztrip404_throwsNotFoundIntegrationException() {
        WebClientResponseException notFound = WebClientResponseException.create(
            404, "Not Found", new HttpHeaders(), new byte[0], null);
        stubPost(Mono.error(notFound));

        PropertyMappingRequest request = PropertyMappingRequest.builder()
            .providerPropertyId(List.of(100567384L))
            .build();

        assertThatThrownBy(() -> service.updatePropertyMapping(callerContext, "unknown-id", request))
            .isInstanceOf(BiztripIntegrationException.class)
            .satisfies(ex -> assertThat(((BiztripIntegrationException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void updatePropertyMapping_biztrip5xx_throwsIntegrationException() {
        WebClientResponseException serverError = WebClientResponseException.create(
            503, "Service Unavailable", new HttpHeaders(), new byte[0], null);
        stubPost(Mono.error(serverError));

        PropertyMappingRequest request = PropertyMappingRequest.builder()
            .providerPropertyId(List.of(100567384L))
            .build();

        assertThatThrownBy(() -> service.updatePropertyMapping(callerContext, "9409190", request))
            .isInstanceOf(BiztripIntegrationException.class)
            .satisfies(ex -> assertThat(((BiztripIntegrationException) ex).getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY));
    }
}
