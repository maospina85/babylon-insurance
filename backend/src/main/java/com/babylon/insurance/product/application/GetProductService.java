package com.babylon.insurance.product.application;

import com.babylon.insurance.product.domain.model.Product;
import com.babylon.insurance.product.domain.port.in.GetProductUseCase;
import com.babylon.insurance.product.domain.port.out.ProductRepositoryPort;
import com.babylon.insurance.shared.exception.ResourceNotFoundException;
import com.babylon.insurance.shared.logging.StructuredLogger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Application service that implements {@link GetProductUseCase}.
 */
@Service
public class GetProductService implements GetProductUseCase {

    private final ProductRepositoryPort repository;
    private final StructuredLogger log;

    public GetProductService(ProductRepositoryPort repository, StructuredLogger log) {
        this.repository = repository;
        this.log        = log;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Emits a structured log event {@code product_fetched} on success.
     *
     * @throws ResourceNotFoundException if no active product exists in the catalogue
     */
    @Override
    public Mono<Product> findLatestActive() {
        return repository.findLatestActive()
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("No se encontró un producto activo en el catálogo")))
                .doOnSuccess(p -> log.info(
                        "product_fetched",
                        "system",
                        Map.of("version", p.version(), "modules", p.modules().size())));
    }
}
