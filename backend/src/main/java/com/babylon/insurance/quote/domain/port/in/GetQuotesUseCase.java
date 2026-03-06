package com.babylon.insurance.quote.domain.port.in;

import com.babylon.insurance.quote.domain.model.Quote;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Input port: retrieves existing quotes / policies.
 */
public interface GetQuotesUseCase {

    /**
     * Returns all quotes ordered by creation date descending.
     *
     * @return a {@link Flux} of all persisted quotes; empty flux if none exist
     */
    Flux<Quote> findAll();

    /**
     * Looks up a single quote by its policy number.
     *
     * @param policyNumber the BLF-XXXXXXX formatted policy number
     * @return the matching {@link Quote}, or an error signal if not found
     */
    Mono<Quote> findByPolicyNumber(String policyNumber);
}
