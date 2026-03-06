package com.babylon.insurance.product.domain.port.out;

import com.babylon.insurance.product.domain.model.Product;
import reactor.core.publisher.Mono;

/**
 * Output port: persistence contract for {@link Product} catalogue versions.
 */
public interface ProductRepositoryPort {

    /**
     * Finds the most recently created active product version.
     *
     * @return the latest active product, or an empty {@link Mono} if none exists
     */
    Mono<Product> findLatestActive();

    /**
     * Persists a product version (used during catalogue initialisation).
     *
     * @param product the product to save
     * @return the saved product
     */
    Mono<Product> save(Product product);
}
