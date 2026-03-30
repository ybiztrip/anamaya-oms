package ai.anamaya.service.oms.core.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication()
            .getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}