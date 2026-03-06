package com.babylon.insurance.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration for the WebFlux stack.
 *
 * <p>Allowed origins are read from the {@code ALLOWED_ORIGINS} environment variable
 * (comma-separated) to avoid hard-coding frontend URLs in the codebase.
 */
@Configuration
public class CorsConfig {

    /**
     * Creates a {@link CorsWebFilter} that applies the policy to all routes.
     *
     * @param allowedOrigins comma-separated list of permitted origins
     * @return the configured CORS filter
     */
    @Bean
    public CorsWebFilter corsWebFilter(
            @Value("${babylon.cors.allowed-origins:http://localhost:3000}") String allowedOrigins) {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "X-Correlation-ID", "Authorization"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
