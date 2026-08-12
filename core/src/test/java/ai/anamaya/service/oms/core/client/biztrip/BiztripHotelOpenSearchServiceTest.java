package ai.anamaya.service.oms.core.client.biztrip;

import ai.anamaya.service.oms.core.context.CallerContext;
import ai.anamaya.service.oms.core.context.UserCallerContext;
import ai.anamaya.service.oms.core.dto.request.UpdateHotelOpenSearchRequest;
import ai.anamaya.service.oms.core.dto.response.HotelOpenSearchResponse;
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
import java.math.BigDecimal;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BiztripHotelOpenSearchServiceTest {

    private static final String PATH = "/hotel/admin/opensearch/{id}";
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

    private BiztripHotelOpenSearchService service;

    @BeforeEach
    void setUp() {
        service = new BiztripHotelOpenSearchService(webClient, authService, new ObjectMapper());
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
    private void stubPut(Mono<String> response) {
        when(authService.getAccessToken(1L)).thenReturn(TOKEN);
        when(webClient.put()).thenReturn(postUriSpec);
        when(postUriSpec.uri(eq(PATH), any(Object[].class))).thenReturn(postBodySpec);
        when(postBodySpec.header(eq(HttpHeaders.AUTHORIZATION), eq(TOKEN))).thenReturn(postBodySpec);
        when(postBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(postBodySpec);
        when(postBodySpec.bodyValue(any())).thenReturn((WebClient.RequestHeadersSpec) postHeadersSpec);
        when(postHeadersSpec.retrieve()).thenReturn(postResponseSpec);
        when(postResponseSpec.bodyToMono(String.class)).thenReturn(response);
    }

    private static String successJson() {
        return """
            {
              "success": true,
              "data": {
                "id": "9409190",
                "name": "Hotel Daisy",
                "star": 3,
                "estimationPrice": 300000,
                "address": "[\\"Via Dott. F. Garofoli, 294\\"]",
                "province": "VR",
                "city": "San Giovanni Lupatoto",
                "countryCode": "IT",
                "postalCode": "37057",
                "latitude": 45.396868,
                "longitude": 11.025483,
                "rank": 286700,
                "accommodationType": "INN"
              }
            }
            """;
    }

    @SuppressWarnings("unchecked")
    @Test
    void getOpenSearch_callsBiztripAndReturnsMappedResponse() {
        stubGet(Mono.just(successJson()));

        HotelOpenSearchResponse result = service.getOpenSearch(callerContext, "9409190");

        assertThat(result.getId()).isEqualTo("9409190");
        assertThat(result.getName()).isEqualTo("Hotel Daisy");
        assertThat(result.getStar()).isEqualTo(3);
        assertThat(result.getEstimationPrice()).isEqualByComparingTo(new BigDecimal("300000"));
        assertThat(result.getAccommodationType()).isEqualTo("INN");

        ArgumentCaptor<Object[]> idCaptor = ArgumentCaptor.forClass(Object[].class);
        verify(getUriSpec).uri(eq(PATH), idCaptor.capture());
        assertThat(idCaptor.getValue()).containsExactly("9409190");
    }

    @Test
    void getOpenSearch_biztrip404_throwsNotFoundIntegrationException() {
        WebClientResponseException notFound = WebClientResponseException.create(
            404, "Not Found", new HttpHeaders(), new byte[0], null);
        stubGet(Mono.error(notFound));

        assertThatThrownBy(() -> service.getOpenSearch(callerContext, "unknown-id"))
            .isInstanceOf(BiztripIntegrationException.class)
            .satisfies(ex -> assertThat(((BiztripIntegrationException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void getOpenSearch_biztrip5xx_throwsIntegrationException() {
        WebClientResponseException serverError = WebClientResponseException.create(
            500, "Internal Server Error", new HttpHeaders(), new byte[0], null);
        stubGet(Mono.error(serverError));

        assertThatThrownBy(() -> service.getOpenSearch(callerContext, "9409190"))
            .isInstanceOf(BiztripIntegrationException.class)
            .satisfies(ex -> assertThat(((BiztripIntegrationException) ex).getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY));
    }

    @Test
    void getOpenSearch_connectionFailure_throwsServiceUnavailable() {
        WebClientRequestException connectionFailure = new WebClientRequestException(
            new IOException("Connection refused"), HttpMethod.GET,
            URI.create("http://biztrip.test/hotel/admin/opensearch/9409190"), new HttpHeaders());
        stubGet(Mono.error(connectionFailure));

        assertThatThrownBy(() -> service.getOpenSearch(callerContext, "9409190"))
            .isInstanceOf(BiztripIntegrationException.class)
            .satisfies(ex -> assertThat(((BiztripIntegrationException) ex).getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE));
    }

    @Test
    void getOpenSearch_invalidJsonResponse_throwsBadGateway() {
        stubGet(Mono.just("not-json"));

        assertThatThrownBy(() -> service.getOpenSearch(callerContext, "9409190"))
            .isInstanceOf(BiztripIntegrationException.class)
            .satisfies(ex -> assertThat(((BiztripIntegrationException) ex).getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY));
    }

    @SuppressWarnings("unchecked")
    @Test
    void updateOpenSearch_forwardsIdAndBodyFields_returnsMappedResponse() {
        stubPut(Mono.just(successJson()));

        UpdateHotelOpenSearchRequest request = sampleUpdateRequest();

        HotelOpenSearchResponse result = service.updateOpenSearch(callerContext, "9409190", request);

        assertThat(result.getId()).isEqualTo("9409190");
        assertThat(result.getStar()).isEqualTo(3);
        assertThat(result.getEstimationPrice()).isEqualByComparingTo(new BigDecimal("300000"));

        ArgumentCaptor<Object[]> idCaptor = ArgumentCaptor.forClass(Object[].class);
        verify(postUriSpec).uri(eq(PATH), idCaptor.capture());
        assertThat(idCaptor.getValue()).containsExactly("9409190");

        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(postBodySpec).bodyValue(bodyCaptor.capture());
        UpdateHotelOpenSearchRequest forwarded = (UpdateHotelOpenSearchRequest) bodyCaptor.getValue();
        assertThat(forwarded.getStar()).isEqualTo(3);
        assertThat(forwarded.getEstimationPrice()).isEqualByComparingTo(new BigDecimal("300000"));
        assertThat(forwarded).isSameAs(request);
    }

    @Test
    void updateOpenSearch_biztrip404_throwsNotFoundIntegrationException() {
        WebClientResponseException notFound = WebClientResponseException.create(
            404, "Not Found", new HttpHeaders(), new byte[0], null);
        stubPut(Mono.error(notFound));

        UpdateHotelOpenSearchRequest request = sampleUpdateRequest();

        assertThatThrownBy(() -> service.updateOpenSearch(callerContext, "unknown-id", request))
            .isInstanceOf(BiztripIntegrationException.class)
            .satisfies(ex -> assertThat(((BiztripIntegrationException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void updateOpenSearch_biztrip5xx_throwsIntegrationException() {
        WebClientResponseException serverError = WebClientResponseException.create(
            503, "Service Unavailable", new HttpHeaders(), new byte[0], null);
        stubPut(Mono.error(serverError));

        UpdateHotelOpenSearchRequest request = sampleUpdateRequest();

        assertThatThrownBy(() -> service.updateOpenSearch(callerContext, "9409190", request))
            .isInstanceOf(BiztripIntegrationException.class)
            .satisfies(ex -> assertThat(((BiztripIntegrationException) ex).getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY));
    }

    private static UpdateHotelOpenSearchRequest sampleUpdateRequest() {
        return UpdateHotelOpenSearchRequest.builder()
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
}
