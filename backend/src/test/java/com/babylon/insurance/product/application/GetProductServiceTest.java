package com.babylon.insurance.product.application;

import com.babylon.insurance.product.domain.model.Product;
import com.babylon.insurance.product.domain.model.ProductStatus;
import com.babylon.insurance.product.domain.port.out.ProductRepositoryPort;
import com.babylon.insurance.shared.exception.ResourceNotFoundException;
import com.babylon.insurance.shared.logging.StructuredLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetProductService")
class GetProductServiceTest {

    @Mock private ProductRepositoryPort repository;
    @Mock private StructuredLogger log;

    private GetProductService service;

    @BeforeEach
    void setUp() {
        service = new GetProductService(repository, log);
        // lenient: not all tests will trigger a log.info call (e.g. the error path)
        lenient().doNothing().when(log).info(anyString(), anyString(), anyMap());
    }

    @Test
    @DisplayName("dado que existe producto activo cuando findLatestActive entonces retorna el producto")
    void givenActiveProduct_whenFindLatestActive_thenReturnProduct() {
        Product product = buildProduct();
        when(repository.findLatestActive()).thenReturn(Mono.just(product));

        StepVerifier.create(service.findLatestActive())
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    @DisplayName("dado que no existe producto cuando findLatestActive entonces emite error 404")
    void givenNoProduct_whenFindLatestActive_thenEmitResourceNotFoundException() {
        when(repository.findLatestActive()).thenReturn(Mono.empty());

        StepVerifier.create(service.findLatestActive())
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private Product buildProduct() {
        return new Product("BABYLON_LIFE", "2025.1", ProductStatus.ACTIVE, List.of(), Instant.now());
    }
}
