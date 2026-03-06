package com.babylon.insurance.quote.domain.port.in;

import com.babylon.insurance.quote.domain.model.Quote;
import reactor.core.publisher.Mono;

/**
 * Input port: creates an insurance quote and issues the policy.
 *
 * <p>Implementations must:
 * <ul>
 *   <li>Calculate the total monthly premium from the selected coverages.</li>
 *   <li>Generate a unique policy number in {@code BLF-XXXXXXX} format.</li>
 *   <li>Encrypt holder PII fields before persistence.</li>
 *   <li>Persist the resulting {@link Quote} with status {@code ISSUED}.</li>
 *   <li>Emit a structured log event {@code quote_issued} without PII.</li>
 * </ul>
 */
public interface CreateQuoteUseCase {

    /**
     * Creates and persists a new insurance quote / policy.
     *
     * @param command       validated input data from the caller
     * @param correlationId X-Correlation-ID for end-to-end traceability
     * @return the persisted {@link Quote}, wrapped in a {@link Mono}
     */
    Mono<Quote> execute(CreateQuoteCommand command, String correlationId);
}
