package com.babylon.insurance.shared.correlation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Correlation Filter")
class CorrelationFilterTest {

    private CorrelationFilter filter;
    private WebFilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new CorrelationFilter();
        chain  = mock(WebFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("dado header X-Correlation-ID válido cuando filtra entonces lo propaga en el response")
    void givenValidCorrelationHeader_whenFilter_thenPropagateInResponse() {
        String validId = "abc-1234-5678-xyz";
        var request  = MockServerHttpRequest.get("/test")
                .header("X-Correlation-ID", validId).build();
        var exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(exchange.getResponse().getHeaders().getFirst("X-Correlation-ID"))
                .isEqualTo(validId);
    }

    @Test
    @DisplayName("dado header con caracteres inválidos cuando filtra entonces genera nuevo UUID")
    void givenInvalidCorrelationHeader_whenFilter_thenGenerateNewUuid() {
        String invalid = "<script>alert(1)</script>";
        var request  = MockServerHttpRequest.get("/test")
                .header("X-Correlation-ID", invalid).build();
        var exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        String resultId = exchange.getResponse().getHeaders().getFirst("X-Correlation-ID");
        assertThat(resultId).isNotNull().isNotEqualTo(invalid);
        assertThat(resultId).matches("[a-fA-F0-9\\-]{36}"); // UUID format
    }

    @Test
    @DisplayName("dado sin header X-Correlation-ID cuando filtra entonces genera UUID y lo agrega al response")
    void givenNoCorrelationHeader_whenFilter_thenGenerateAndAddToResponse() {
        var request  = MockServerHttpRequest.get("/test").build();
        var exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        String resultId = exchange.getResponse().getHeaders().getFirst("X-Correlation-ID");
        assertThat(resultId).isNotNull().isNotBlank();
        // Should be a valid UUID
        assertThat(resultId).matches("[a-fA-F0-9\\-]{36}");
    }

    @Test
    @DisplayName("dado header de 7 caracteres (demasiado corto) cuando filtra entonces genera nuevo UUID")
    void givenTooShortCorrelationHeader_whenFilter_thenGenerateNewUuid() {
        var request  = MockServerHttpRequest.get("/test")
                .header("X-Correlation-ID", "1234567").build(); // 7 chars — minimum is 8
        var exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        String resultId = exchange.getResponse().getHeaders().getFirst("X-Correlation-ID");
        assertThat(resultId).isNotEqualTo("1234567");
    }
}
