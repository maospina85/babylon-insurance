package com.babylon.insurance.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;

/**
 * Spring Security configuration for the WebFlux stack.
 *
 * <p>Implements OWASP Top 10 mitigations:
 * <ul>
 *   <li><strong>A01</strong> — Routes protected by default; only catalogue and health are public.
 *       Quote API is also permitted in the demo; restrict to authenticated users in production.</li>
 *   <li><strong>A05</strong> — Security headers: CSP, X-Frame-Options, HSTS, Referrer-Policy.</li>
 *   <li>CSRF disabled — this is a stateless REST API.</li>
 * </ul>
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Configures the reactive security filter chain.
     *
     * @param http the {@link ServerHttpSecurity} to configure
     * @return the built {@link SecurityWebFilterChain}
     */
    @Bean
    @Order(-100)
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(auth -> auth
                        // CORS preflight — must be permitted before security applies
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Public: product catalogue and health checks
                        .pathMatchers(HttpMethod.GET,
                                "/api/products/catalog",
                                "/actuator/health",
                                "/actuator/info").permitAll()
                        // Demo: quote API open; extend with JWT in production
                        .pathMatchers("/api/quotes/**").permitAll()
                        // All other routes denied by default (A01)
                        .anyExchange().denyAll()
                )
                .headers(headers -> headers
                        // A05: X-Frame-Options DENY
                        .frameOptions(frame ->
                                frame.mode(XFrameOptionsServerHttpHeadersWriter.Mode.DENY))
                        // A05: Content Security Policy
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives(
                                        "default-src 'self'; " +
                                        "frame-ancestors 'none'; " +
                                        "script-src 'self'"))
                        // A05: Referrer-Policy
                        .referrerPolicy(referrer ->
                                referrer.policy(ReferrerPolicyServerHttpHeadersWriter
                                        .ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        // A05: HSTS (enforced only over HTTPS in production)
                        .hsts(hsts -> hsts
                                .maxAge(java.time.Duration.ofDays(365))
                                .includeSubdomains(true))
                )
                .build();
    }
}
