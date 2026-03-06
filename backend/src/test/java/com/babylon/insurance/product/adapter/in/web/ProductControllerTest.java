package com.babylon.insurance.product.adapter.in.web;

import com.babylon.insurance.product.domain.model.CoverageTier;
import com.babylon.insurance.product.domain.model.DeathCoverage;
import com.babylon.insurance.product.domain.model.InsuranceModule;
import com.babylon.insurance.product.domain.model.Product;
import com.babylon.insurance.product.domain.model.ProductStatus;
import com.babylon.insurance.product.domain.port.in.GetProductUseCase;
import com.babylon.insurance.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController")
class ProductControllerTest {

    @Mock private GetProductUseCase getProductUseCase;

    private WebTestClient webClient;

    @BeforeEach
    void setUp() {
        webClient = WebTestClient
                .bindToController(new ProductController(getProductUseCase))
                .build();
    }

    @Test
    @DisplayName("dado producto activo cuando GET /api/products/catalog entonces retorna 200 con catálogo")
    void givenActiveProduct_whenGetCatalog_thenReturn200WithProductCode() {
        when(getProductUseCase.findLatestActive()).thenReturn(Mono.just(buildProduct()));

        webClient.get().uri("/api/products/catalog")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.productCode").isEqualTo("BABYLON_LIFE")
                .jsonPath("$.version").isEqualTo("2025.1")
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("dado respuesta con Cache-Control cuando GET /api/products/catalog entonces tiene header max-age=300")
    void givenProduct_whenGetCatalog_thenResponseHasCacheControlHeader() {
        when(getProductUseCase.findLatestActive()).thenReturn(Mono.just(buildProduct()));

        webClient.get().uri("/api/products/catalog")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueMatches("Cache-Control", ".*max-age=300.*");
    }

    @Test
    @DisplayName("dado catálogo vacío cuando GET /api/products/catalog entonces retorna 5xx")
    void givenNoProduct_whenGetCatalog_thenReturnErrorStatus() {
        when(getProductUseCase.findLatestActive())
                .thenReturn(Mono.error(new ResourceNotFoundException("No hay producto activo")));

        // Without GlobalExceptionHandler in this unit test, Spring returns 500 for unhandled errors
        webClient.get().uri("/api/products/catalog")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private Product buildProduct() {
        DeathCoverage coverage = new DeathCoverage("death_main", "Fallecimiento", "Descripción test");
        InsuranceModule module = new InsuranceModule(
                "death", "Vida / Fallecimiento", "🛡️", "death",
                "Descripción de prueba",
                true, "Cobertura de Fallecimiento",
                List.of(new CoverageTier("t1", "Esencial", java.math.BigDecimal.valueOf(10_000_000),
                        java.math.BigDecimal.valueOf(12_500), List.of())),
                List.of(coverage));
        return new Product("BABYLON_LIFE", "2025.1", ProductStatus.ACTIVE, List.of(module), Instant.now());
    }
}
