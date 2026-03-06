package com.babylon.insurance.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * WebFlux configuration.
 *
 * <p>Sets the maximum in-memory buffer size for request/response codecs
 * to prevent excessive memory usage from large payloads.
 */
@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    private static final int MAX_IN_MEMORY_SIZE = 1024 * 1024; // 1 MiB

    /**
     * Limits codec buffer size to 1 MiB to mitigate large-payload DoS attempts.
     *
     * @param configurer the codec configurer provided by the framework
     */
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE);
    }
}
