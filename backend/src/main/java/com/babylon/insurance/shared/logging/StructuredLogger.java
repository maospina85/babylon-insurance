package com.babylon.insurance.shared.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Structured logging wrapper that guarantees:
 * <ul>
 *   <li>Every log entry includes the {@code correlationId} in MDC.</li>
 *   <li>Log events are named in {@code snake_case}.</li>
 *   <li>PII fields ({@code holderName}, {@code holderEmail}, passwords, card numbers)
 *       are never passed to this class — callers are responsible for exclusion.</li>
 * </ul>
 *
 * <p>The Logback JSON appender reads MDC fields and emits them as top-level
 * JSON properties, enabling structured log queries in any log-aggregation platform.
 */
@Component
public class StructuredLogger {

    private static final Logger logger = LoggerFactory.getLogger(StructuredLogger.class);
    private static final String CORRELATION_MDC = "correlationId";
    private static final String EVENT_MDC       = "event";

    /**
     * Logs an informational event with structured context fields.
     *
     * <p>The {@code fields} map must not contain PII values.
     *
     * @param event         snake_case event name (e.g., {@code quote_issued})
     * @param correlationId request traceability identifier
     * @param fields        additional non-PII context key-value pairs
     */
    public void info(String event, String correlationId, Map<String, Object> fields) {
        withMdc(event, correlationId, fields, () -> logger.info(event));
    }

    /**
     * Logs an error event with the full exception and structured context.
     *
     * <p>The stack trace is emitted server-side only and must never be sent
     * to API callers.
     *
     * @param event         snake_case event name
     * @param correlationId request traceability identifier
     * @param ex            the exception to log; stack trace is included
     * @param fields        additional non-PII context key-value pairs
     */
    public void error(String event, String correlationId, Throwable ex,
                      Map<String, Object> fields) {
        withMdc(event, correlationId, fields, () -> logger.error(event, ex));
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private void withMdc(String event, String correlationId, Map<String, Object> fields,
                         Runnable action) {
        MDC.put(CORRELATION_MDC, correlationId);
        MDC.put(EVENT_MDC, event);
        if (fields != null) {
            fields.forEach((k, v) -> MDC.put(k, v == null ? "null" : v.toString()));
        }
        try {
            action.run();
        } finally {
            MDC.remove(EVENT_MDC);
            if (fields != null) {
                fields.keySet().forEach(MDC::remove);
            }
            // correlationId is managed by CorrelationFilter; leave it in MDC
        }
    }
}
