package ai.anamaya.service.oms.core.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtUtils(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Long getCompanyIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getCredentials() == null)
            return null;

        String token = authentication.getCredentials().toString();
        Claims claims = jwtTokenProvider.getClaims(token);
        return claims.get("companyId", Long.class);
    }

    public Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getCredentials() == null)
            return null;

        String token = authentication.getCredentials().toString();
        Claims claims = jwtTokenProvider.getClaims(token);
        return claims.get("userId", Long.class);
    }
}
