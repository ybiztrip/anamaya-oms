package ai.anamaya.service.oms.rest.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_PAYLOAD_LENGTH = 10000;

    @Value("${app.logging.http.request.enabled:true}")
    private boolean logRequest;

    @Value("${app.logging.http.response.enabled:true}")
    private boolean logResponse;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        if (!logRequest && !logResponse) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Prefer GCP's trace id if present so logs line up with Cloud Trace, else generate one.
        String traceId = traceId(request);
        MDC.put("trace_id", traceId);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            String uri = request.getRequestURI();
            String query = request.getQueryString();
            String path = query == null ? uri : uri + "?" + query;

            if (logRequest) {
                log.info("REQUEST DATA: {} {}, payload={}",
                    request.getMethod(), path, body(wrappedRequest.getContentAsByteArray()));
            }
            if (logResponse) {
                log.info("RESPONSE DATA: {} {}, status={}, payload={}",
                    request.getMethod(), path, wrappedResponse.getStatus(),
                    body(wrappedResponse.getContentAsByteArray()));
            }

            // MUST copy the buffered body back, otherwise the client gets an empty response.
            wrappedResponse.copyBodyToResponse();
            MDC.remove("trace_id");
        }
    }

    private String traceId(HttpServletRequest request) {
        // X-Cloud-Trace-Context: TRACE_ID/SPAN_ID;o=1
        String header = request.getHeader("X-Cloud-Trace-Context");
        if (header != null && !header.isBlank()) {
            int slash = header.indexOf('/');
            return slash > 0 ? header.substring(0, slash) : header;
        }
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    private String body(byte[] content) {
        if (content.length == 0) {
            return "";
        }
        int length = Math.min(content.length, MAX_PAYLOAD_LENGTH);
        return new String(content, 0, length, StandardCharsets.UTF_8);
    }
}
