package com.babylon.insurance.product.domain.port.in;

import com.babylon.insurance.product.domain.model.Product;
import reactor.core.publisher.Mono;

/**
 * Input port: retrieves the current active product catalogue.
 */
public interface GetProductUseCase {

    /**
     * Returns the latest active product version.
     *
     * @return the active {@link Product}, or an error signal if none exists
     * @throws com.babylon.insurance.shared.exception.ResourceNotFoundException
     *         if no active product is found in the catalogue
     */
    Mono<Product> findLatestActive();
}
