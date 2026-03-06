package com.babylon.insurance.shared.exception;

import com.babylon.insurance.shared.logging.StructuredLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralised exception handler for all REST controllers.
 *
 * <p>Guarantees that:
 * <ul>
 *   <li>Stack traces never reach the response body.</li>
 *   <li>Every error response includes the {@code correlationId} for traceability.</li>
 *   <li>PII is never logged.</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final StructuredLogger log;

    public GlobalExceptionHandler(StructuredLogger log) {
        this.log = log;
    }

    /**
     * Represents a standardised error response body.
     *
     * @param error         human-readable error summary
     * @param correlationId traceability identifier from the request context
     * @param timestamp     UTC timestamp of the error
     */
    public record ErrorResponse(String error, String correlationId, Instant timestamp) {}

    /**
     * Handles Bean Validation failures ({@code 400 Bad Request}).
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(
            WebExchangeBindException ex, ServerWebExchange exchange) {

        String fields = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return correlationId(exchange).map(cid -> ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Campos inválidos: " + fields, cid, Instant.now())));
    }

    /**
     * Handles domain business-rule violations ({@code 422 Unprocessable Entity}).
     */
    @ExceptionHandler(QuoteValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleQuoteValidation(
            QuoteValidationException ex, ServerWebExchange exchange) {

        return correlationId(exchange).map(cid -> ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(ex.getMessage(), cid, Instant.now())));
    }

    /**
     * Handles missing-resource errors ({@code 404 Not Found}).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(
            ResourceNotFoundException ex, ServerWebExchange exchange) {

        return correlationId(exchange).map(cid -> ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Recurso no encontrado", cid, Instant.now())));
    }

    /**
     * Catch-all handler ({@code 500 Internal Server Error}).
     *
     * <p>Logs the full stack trace server-side but returns only a generic message to the caller.
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(
            Exception ex, ServerWebExchange exchange) {

        return correlationId(exchange).map(cid -> {
            log.error("unhandled_exception", cid, ex, Map.of("exType", ex.getClass().getSimpleName()));
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor", cid, Instant.now()));
        });
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Mono<String> correlationId(ServerWebExchange exchange) {
        return Mono.deferContextual(ctx ->
                Mono.just(ctx.getOrDefault("correlationId",
                        exchange.getRequest().getHeaders().getFirst("X-Correlation-ID") != null
                                ? exchange.getRequest().getHeaders().getFirst("X-Correlation-ID")
                                : "unknown")));
    }
}
