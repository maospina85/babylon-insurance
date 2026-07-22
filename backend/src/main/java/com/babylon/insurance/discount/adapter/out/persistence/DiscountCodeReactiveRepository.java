package com.babylon.insurance.discount.adapter.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Spring Data Reactive MongoDB repository for {@link DiscountCodeDocument}.
 *
 * <p>Used exclusively by {@link DiscountCodeMongoAdapter}.
 */
interface DiscountCodeReactiveRepository extends ReactiveMongoRepository<DiscountCodeDocument, String> {

    /**
     * Finds a discount code document by its code, case-insensitive.
     *
     * @param code the code entered by the holder
     * @return the matching document, or an empty {@link Mono}
     */
    Mono<DiscountCodeDocument> findByCodeIgnoreCase(String code);
}
