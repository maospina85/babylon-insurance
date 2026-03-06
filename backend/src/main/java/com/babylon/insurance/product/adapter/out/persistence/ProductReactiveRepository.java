package com.babylon.insurance.product.adapter.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Spring Data Reactive MongoDB repository for {@link ProductDocument}.
 *
 * <p>Used exclusively by {@link ProductMongoAdapter}.
 */
interface ProductReactiveRepository extends ReactiveMongoRepository<ProductDocument, String> {

    /**
     * Finds the most recently created product document with the given status.
     *
     * @param status the lifecycle status string (e.g., "ACTIVE")
     * @return the matching product, or an empty {@link Mono}
     */
    Mono<ProductDocument> findFirstByStatusOrderByCreatedAtDesc(String status);
}
