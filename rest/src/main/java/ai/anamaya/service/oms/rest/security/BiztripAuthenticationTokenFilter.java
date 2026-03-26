package ai.anamaya.service.oms.rest.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class BiztripAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-BIZTRIP-API-KEY";

    @Value("${security.biztrip.api-key}")
    private String staticToken;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String apiKey = request.getHeader(HEADER);

        if (staticToken.equals(apiKey)) {
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    "biztrip-partner",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_SYSTEM"))
                );

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
