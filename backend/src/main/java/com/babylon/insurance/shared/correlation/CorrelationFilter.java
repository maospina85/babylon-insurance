package com.babylon.insurance.shared.correlation;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * WebFilter that ensures every request carries a valid {@code X-Correlation-ID}.
 *
 * <p>Behaviour:
 * <ol>
 *   <li>Reads the {@code X-Correlation-ID} request header.</li>
 *   <li>Validates the value against {@code [a-zA-Z0-9\-]{8,64}}.
 *       If absent or invalid, a new UUID is generated (A07 — Auth Failures prevention:
 *       prevents log injection via forged correlation IDs).</li>
 *   <li>Adds the sanitised value to the response header.</li>
 *   <li>Propagates it in the Reactor {@link reactor.util.context.Context}
 *       under the key {@code "correlationId"}.</li>
 *   <li>Sets it in {@link MDC} for Logback JSON appender inclusion.</li>
 * </ol>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationFilter implements WebFilter {

    private static final String HEADER            = "X-Correlation-ID";
    private static final Pattern VALID_CORRELATION = Pattern.compile("^[a-zA-Z0-9\\-]{8,64}$");
    private static final String  MDC_KEY           = "correlationId";

    /**
     * Processes the correlation ID and propagates it through the reactive chain.
     *
     * @param exchange the current server exchange
     * @param chain    the rest of the filter chain
     * @return a {@link Mono} that completes when the chain finishes
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String raw  = exchange.getRequest().getHeaders().getFirst(HEADER);
        String safe = isValid(raw) ? raw : UUID.randomUUID().toString();

        exchange.getResponse().getHeaders().add(HEADER, safe);
        MDC.put(MDC_KEY, safe);

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(MDC_KEY, safe))
                .doFinally(signal -> MDC.remove(MDC_KEY));
    }

    private boolean isValid(String value) {
        return value != null && VALID_CORRELATION.matcher(value).matches();
    }
}
