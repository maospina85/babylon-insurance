package com.babylon.insurance.quote.adapter.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Spring Data Reactive MongoDB repository for {@link QuoteDocument}.
 *
 * <p>Used exclusively by {@link QuoteMongoAdapter}; never referenced from
 * the domain or application layers.
 */
interface QuoteReactiveRepository extends ReactiveMongoRepository<QuoteDocument, String> {

    /**
     * Finds a quote document by its unique policy number.
     *
     * @param policyNumber the BLF-XXXXXXX formatted identifier
     * @return the matching document, or an empty {@link Mono}
     */
    Mono<QuoteDocument> findByPolicyNumber(String policyNumber);
}
