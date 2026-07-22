package com.babylon.insurance.quote.adapter.in.web;

import com.babylon.insurance.quote.adapter.in.web.dto.CreateQuoteRequest;
import com.babylon.insurance.quote.adapter.in.web.dto.SelectedCoverageDto;
import com.babylon.insurance.quote.domain.model.Quote;
import com.babylon.insurance.quote.domain.model.QuoteStatus;
import com.babylon.insurance.quote.domain.port.in.CreateQuoteUseCase;
import com.babylon.insurance.quote.domain.port.in.GetQuotesUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuoteController")
class QuoteControllerTest {

    @Mock private CreateQuoteUseCase createQuoteUseCase;
    @Mock private GetQuotesUseCase getQuotesUseCase;

    private WebTestClient webClient;

    @BeforeEach
    void setUp() {
        webClient = WebTestClient
                .bindToController(new QuoteController(createQuoteUseCase, getQuotesUseCase))
                .build();
    }

    @Test
    @DisplayName("dado request válido cuando POST /api/quotes entonces retorna 201 con policyNumber")
    void givenValidRequest_whenPost_thenReturn201WithPolicyNumber() {
        when(createQuoteUseCase.execute(any(), anyString())).thenReturn(Mono.just(buildQuote()));

        webClient.post().uri("/api/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildValidRequest())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.policyNumber").isEqualTo("BLF-TEST01");
    }

    @Test
    @DisplayName("dado holderName vacío cuando POST /api/quotes entonces retorna 400")
    void givenBlankHolderName_whenPost_thenReturn400() {
        CreateQuoteRequest invalid = new CreateQuoteRequest(
                "", "test@test.com", "+57 300 000", LocalDate.of(1990, 1, 1),
                List.of(validCoverageDto()), List.of(), List.of(), "mensual", null);

        webClient.post().uri("/api/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("dado holderEmail inválido cuando POST /api/quotes entonces retorna 400")
    void givenInvalidEmail_whenPost_thenReturn400() {
        CreateQuoteRequest invalid = new CreateQuoteRequest(
                "María García", "not-an-email", "+57 300 000", LocalDate.of(1990, 1, 1),
                List.of(validCoverageDto()), List.of(), List.of(), "mensual", null);

        webClient.post().uri("/api/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("dado moduleId inválido cuando POST /api/quotes entonces retorna 400")
    void givenInvalidModuleId_whenPost_thenReturn400() {
        SelectedCoverageDto badModule = new SelectedCoverageDto(
                "unknown", "t1", BigDecimal.valueOf(12_500), null);
        CreateQuoteRequest invalid = new CreateQuoteRequest(
                "María García", "test@test.com", "+57 300 000", LocalDate.of(1990, 1, 1),
                List.of(badModule), List.of(), List.of(), "mensual", null);

        webClient.post().uri("/api/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("dado tierId inválido cuando POST /api/quotes entonces retorna 400")
    void givenInvalidTierId_whenPost_thenReturn400() {
        SelectedCoverageDto badTier = new SelectedCoverageDto(
                "death", "t9", BigDecimal.valueOf(12_500), null);
        CreateQuoteRequest invalid = new CreateQuoteRequest(
                "María García", "test@test.com", "+57 300 000", LocalDate.of(1990, 1, 1),
                List.of(badTier), List.of(), List.of(), "mensual", null);

        webClient.post().uri("/api/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("cuando GET /api/quotes entonces retorna 200 con lista")
    void whenGetAll_thenReturn200WithList() {
        when(getQuotesUseCase.findAll()).thenReturn(Flux.just(buildQuote()));

        webClient.get().uri("/api/quotes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class).hasSize(1);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Quote buildQuote() {
        return new Quote(
                "q-uuid", "corr-id", "ENC_NAME", "ENC_EMAIL",
                "+57 300", "1990-01-01",
                List.of(new com.babylon.insurance.quote.domain.model.SelectedCoverage(
                        "death", "t1",
                        BigDecimal.valueOf(12_500),
                        BigDecimal.valueOf(10_000_000))),
                List.of(), List.of("medico_virtual"),
                BigDecimal.valueOf(12_500), "mensual", "BLF-TEST01",
                QuoteStatus.ISSUED, Instant.now(), null);
    }

    private CreateQuoteRequest buildValidRequest() {
        return new CreateQuoteRequest(
                "María García López",
                "maria@example.com",
                "+57 300 000 0000",
                LocalDate.of(1990, 6, 15),
                List.of(validCoverageDto()),
                List.of(), List.of("medico_virtual"), "mensual", null);
    }

    private SelectedCoverageDto validCoverageDto() {
        return new SelectedCoverageDto("death", "t1",
                BigDecimal.valueOf(12_500), BigDecimal.valueOf(10_000_000));
    }
}
