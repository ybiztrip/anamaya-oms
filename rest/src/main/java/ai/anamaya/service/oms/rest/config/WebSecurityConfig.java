package ai.anamaya.service.oms.rest.config;

import ai.anamaya.service.oms.rest.security.BiztripAuthenticationTokenFilter;
import ai.anamaya.service.oms.rest.security.JwtAuthenticationFilter;
import ai.anamaya.service.oms.rest.security.OfficelessAuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final BiztripAuthenticationTokenFilter biztripAuthenticationTokenFilter;
    private final OfficelessAuthenticationTokenFilter officelessAuthenticationTokenFilter;

    public WebSecurityConfig(
        JwtAuthenticationFilter jwtAuthenticationFilter,
        BiztripAuthenticationTokenFilter biztripAuthenticationTokenFilter,
        OfficelessAuthenticationTokenFilter officelessAuthenticationTokenFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.biztripAuthenticationTokenFilter = biztripAuthenticationTokenFilter;
        this.officelessAuthenticationTokenFilter = officelessAuthenticationTokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        CorsConfigurationSource corsConfigurationSource
        ) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(biztripAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(officelessAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .cors(cors -> cors.configurationSource(corsConfigurationSource));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
