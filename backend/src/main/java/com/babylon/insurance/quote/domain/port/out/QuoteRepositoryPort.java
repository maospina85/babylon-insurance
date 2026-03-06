package com.babylon.insurance.quote.domain.port.out;

import com.babylon.insurance.quote.domain.model.Quote;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Output port: persistence contract for {@link Quote} aggregates.
 *
 * <p>Implementations live in the persistence adapter layer and must never
 * be referenced directly from domain model classes.
 */
public interface QuoteRepositoryPort {

    /**
     * Persists a new quote or updates an existing one.
     *
     * @param quote the quote aggregate to save
     * @return the saved quote as returned by the persistence store
     */
    Mono<Quote> save(Quote quote);

    /**
     * Returns all persisted quotes ordered by creation date descending.
     *
     * @return a {@link Flux} of quotes; empty if none exist
     */
    Flux<Quote> findAll();

    /**
     * Finds a quote by its unique policy number.
     *
     * @param policyNumber the BLF-XXXXXXX formatted identifier
     * @return the matching quote, or an empty {@link Mono} if not found
     */
    Mono<Quote> findByPolicyNumber(String policyNumber);
}
